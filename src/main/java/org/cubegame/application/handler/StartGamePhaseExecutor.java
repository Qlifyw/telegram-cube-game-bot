package org.cubegame.application.handler;

import org.cubegame.application.model.IterableResult;
import org.cubegame.application.model.PhaseStatebleResponse;
import org.cubegame.application.model.ProcessedResult;
import org.cubegame.application.model.SkipedResult;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.exceptions.GameNoFoundException;
import org.cubegame.domain.model.dice.Dice;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.game.Player;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.GameId;
import org.cubegame.domain.model.identifier.UserId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.domain.model.round.Outcome;
import org.cubegame.domain.model.round.Outcomes;
import org.cubegame.domain.model.round.Points;
import org.cubegame.domain.model.round.Round;
import org.cubegame.domain.model.session.GameSession;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.TextResponseMessage;
import org.cubegame.infrastructure.repository.game.GameRepository;
import org.cubegame.infrastructure.repository.round.RoundRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class StartGamePhaseExecutor implements PhaseExecutor {

    private final ChatId chatId;
    private final GameRepository gameRepository;
    private final RoundRepository roundRepository;

    private final Game storedGame;
    private final Set<UserId> playersIds;

    private final int numberOfRounds;
    private Outcomes outcomes = new Outcomes();
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
                .get(this.chatId)
                .orElseThrow(() -> new GameNoFoundException(this.chatId));

        this.gameSession = new GameSession(storedGame.getGameId());

        this.playersIds = this.storedGame
                .getPlayers()
                .stream()
                .map(Player::getUserId)
                .collect(Collectors.toSet());

        this.numberOfRounds = this.storedGame.getNumberOfRounds();
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
    public PhaseStatebleResponse execute(Message message) {

        if (message.hasDice()) {
            final Dice dice = message.getDice();

            if (!playersIds.contains(message.getAuthor().getUserId()))
                return new SkipedResult();

            final Round currentRound = gameSession.getActiveRound();
            final Outcomes currentRoundOutcomes = currentRound.getResults();

            if (currentRoundOutcomes.contains(message.getAuthor().getUserId()))
                return new SkipedResult();

            final Player newPlayer = new Player(message.getAuthor().getUserId(), message.getAuthor().getFirstName());
            final Outcome playerOutcome = new Outcome(newPlayer, new Points(dice.getValue()));
            currentRoundOutcomes.add(playerOutcome);

            if (currentRoundOutcomes.size() == playersIds.size()) {
                final String results = currentRoundOutcomes.stream()
                        .map(outcome -> String.format("User %s has %d point", outcome.getPlayer().getFirstName(), outcome.getPoints().getAmount()))
                        .collect(Collectors.joining("\n"));

                final String resultBuilder = "Round results:" + "\n" + results;

                roundRepository.save(currentRound);
                gameSession.completeActiveRound();


                // ============

                // TODO hasWinner()
                final Map<Player, Long> collect = gameSession.getAllRounds()
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


                switch (winners.size()) {
                    case 0:
                        break;
                    case 1:
                        return new ProcessedResult(
                                new TextResponseMessage(winners.get(0).getFirstName(), message.getChatId())
                        );
                    default:
                        break;
                }

                // ============


                return new IterableResult(
                        new TextResponseMessage(resultBuilder, message.getChatId())
                );
            }


        }


        return new SkipedResult();
    }

    @Override
    public Phase getPhase() {
        return Phase.STARTED;
    }

    public static void main(String[] args) {
        final GameId gameId = new GameId(UUID.randomUUID());

        final Outcomes outcomes1 = new Outcomes();
        outcomes1.add(new Outcome(new Player(new UserId(1l), "1"), new Points(1)));
        outcomes1.add(new Outcome(new Player(new UserId(2l), "2"), new Points(2)));
        outcomes1.add(new Outcome(new Player(new UserId(3l), "3"), new Points(3)));

        final Outcomes outcomes2 = new Outcomes();
        outcomes2.add(new Outcome(new Player(new UserId(1l), "1"), new Points(1)));
        outcomes2.add(new Outcome(new Player(new UserId(2l), "2"), new Points(2)));
        outcomes2.add(new Outcome(new Player(new UserId(3l), "3"), new Points(5)));


        final Outcomes outcomes3 = new Outcomes();
        outcomes3.add(new Outcome(new Player(new UserId(1l), "1"), new Points(6)));
        outcomes3.add(new Outcome(new Player(new UserId(2l), "2"), new Points(2)));
        outcomes3.add(new Outcome(new Player(new UserId(3l), "3"), new Points(3)));

        final Outcomes outcomes4 = new Outcomes();
        outcomes4.add(new Outcome(new Player(new UserId(1l), "1"), new Points(1)));
        outcomes4.add(new Outcome(new Player(new UserId(2l), "2"), new Points(6)));
        outcomes4.add(new Outcome(new Player(new UserId(3l), "3"), new Points(3)));


        final List<Round> rounds = new ArrayList();
        rounds.add(new Round(outcomes1, gameId));
        rounds.add(new Round(outcomes2, gameId));
        rounds.add(new Round(outcomes3, gameId));
        rounds.add(new Round(outcomes4, gameId));


        final Map<Player, Long> collect = rounds
                .stream()
                .map(Round::getWinner)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.groupingBy(player -> player, Collectors.counting()));

        final Long max = Collections.max(collect.values());
        final LinkedHashMap<Object, Object> objectObjectLinkedHashMap = new LinkedHashMap<>();


//        final OptionalLong max = collect.entrySet()
//                .stream()
//                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey))
//                .keySet().stream()
//                .mapToLong(l -> l)
//                .max();

//        System.out.println(max);


        System.out.println(collect.size());
        System.out.println(collect);
    }

}
