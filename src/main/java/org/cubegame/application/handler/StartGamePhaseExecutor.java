package org.cubegame.application.handler;

import org.cubegame.application.model.IterableResult;
import org.cubegame.application.model.PhaseResponse;
import org.cubegame.application.model.ProcessedResult;
import org.cubegame.application.model.SkipedResult;
import org.cubegame.domain.exceptions.GameNoFoundException;
import org.cubegame.domain.model.dice.Dice;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.game.GameBuilder;
import org.cubegame.domain.model.game.Player;
import org.cubegame.domain.model.game.state.Phase;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.UserId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.domain.model.round.Outcome;
import org.cubegame.domain.model.round.Outcomes;
import org.cubegame.domain.model.round.Points;
import org.cubegame.domain.model.round.Round;
import org.cubegame.domain.model.session.GameSession;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.TextResponseMessage;
import org.cubegame.infrastructure.repositories.game.GameRepository;
import org.cubegame.infrastructure.repositories.round.RoundRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class StartGamePhaseExecutor implements PhaseExecutor {

    private static final int NO_WINNER = 0;
    private static final int HAS_WINNER = 1;

    private final ChatId chatId;
    private final GameRepository gameRepository;
    private final RoundRepository roundRepository;

    private final Game storedGame;
    private final Set<UserId> invitedPlayersIds;

    private final GameSession gameSession;

    public StartGamePhaseExecutor(
            final ChatId chatId,
            final GameRepository gameRepository,
            final RoundRepository roundRepository
    ) {
        this.gameRepository = gameRepository;
        this.roundRepository = roundRepository;
        this.chatId = chatId;

        this.storedGame = gameRepository
                .getActive(this.chatId)
                .orElseThrow(() -> new GameNoFoundException(this.chatId));

        this.gameSession = new GameSession(storedGame.getGameId());

        this.invitedPlayersIds = this.storedGame
                .getPlayers()
                .stream()
                .map(Player::getUserId)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<ResponseMessage> initiation() {
        final TextResponseMessage initMessage = new TextResponseMessage(
                "Congratulation! Game is started",
                chatId
        );
        return Optional.of(initMessage);
    }

    @Override
    public PhaseResponse execute(Message message) {

        if (message.hasDice()) {
            final Dice dice = message.getDice();

            if (!invitedPlayersIds.contains(message.getAuthor().getUserId()))
                return new SkipedResult();

            final Round currentRound = gameSession.getActiveRound();
            final Outcomes currentRoundOutcomes = currentRound.getResults();
            final UserId currentPlayer = message.getAuthor().getUserId();

            if (currentRoundOutcomes.contains(currentPlayer))
                return new SkipedResult();

            final Player newPlayer = new Player(currentPlayer, message.getAuthor().getFirstName());
            final Outcome playerOutcome = new Outcome(newPlayer, new Points(dice.getValue()));
            currentRoundOutcomes.add(playerOutcome);

            if (currentRoundOutcomes.size() == invitedPlayersIds.size()) {
                final String outcomeTextRepresentation = summarize(currentRoundOutcomes);

                roundRepository.save(currentRound);
                gameSession.completeActiveRound();

                final List<Player> winners = getWinners(gameSession.getAllRounds());

                switch (winners.size()) {
                    case HAS_WINNER:
                        final Phase nextPhase = Phase.getNextFor(getPhase());
                        final Game updatedGame = GameBuilder.from(storedGame)
                                .setPhase(nextPhase)
                                .build();
                        gameRepository.save(updatedGame);

                        return new ProcessedResult(
                                new TextResponseMessage(winners.get(0).getFirstName() + " win", message.getChatId())
                        );
                    case NO_WINNER:
                        break;
                    default:
                        // Need overtime
                        break;
                }

                awaitForLastPlayerAnimation();
                return new IterableResult(
                        new TextResponseMessage(outcomeTextRepresentation, message.getChatId())
                );
            }


        }


        return new SkipedResult();
    }

    @Override
    public Phase getPhase() {
        return Phase.STARTED;
    }

    private String summarize(Outcomes outcomes) {
        final String results = outcomes.stream()
                .map(outcome -> String.format("User %s has %d point", outcome.getPlayer().getFirstName(), outcome.getPoints().getAmount()))
                .collect(Collectors.joining("\n"));

        return "Round results:" + "\n" + results;
    }

    private List<Player> getWinners(List<Round> rounds) {
        final Map<Player, Long> collect = rounds
                .stream()
                .map(Round::getWinner)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.groupingBy(player -> player, Collectors.counting()));

        final Long max = Collections.max(collect.values());
        final List<Player> winners = new ArrayList<>();

        collect.forEach((player, aLong) -> {
            if ((max == storedGame.getNumberOfRounds()) && (aLong.equals(max)))
                winners.add(player);
        });

        return winners;
    }

    private void awaitForLastPlayerAnimation() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
