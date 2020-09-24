package org.cubegame.application.handler;

import org.cubegame.application.model.PhaseStatebleResponse;
import org.cubegame.application.model.ProcessedResult;
import org.cubegame.application.model.SkipedResult;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.exceptions.GameNoFoundException;
import org.cubegame.domain.model.dice.Dice;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.game.Player;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.UserId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.domain.model.round.Outcome;
import org.cubegame.domain.model.round.Outcomes;
import org.cubegame.domain.model.round.Points;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.TextResponseMessage;
import org.cubegame.infrastructure.repository.game.GameRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class StartGamePhaseExecutor implements PhaseExecutor {

    private final ChatId chatId;
    private final GameRepository gameRepository;

    private final Game storedGame;
    private final Set<UserId> playersIds;

    private Outcomes outcomes = new Outcomes();

    public StartGamePhaseExecutor(final ChatId chatId, final GameRepository gameRepository) {
        this.gameRepository = gameRepository;
        this.chatId = chatId;

        this.storedGame = gameRepository
                .get(this.chatId)
                .orElseThrow(() -> new GameNoFoundException(this.chatId));

        this.playersIds = this.storedGame
                .getPlayers()
                .stream()
                .map(Player::getUserId)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<ResponseMessage> initiation(ChatId chatId) {
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

            if (outcomes.contains(message.getAuthor().getUserId()))
                return new SkipedResult();

            final Player newPlayer = new Player(message.getAuthor().getUserId(), message.getAuthor().getFirstName());
            final Outcome playerOutcome = new Outcome(newPlayer, new Points(dice.getValue()));
            outcomes.add(playerOutcome);

            if (outcomes.size() == playersIds.size()) {
                final String results = outcomes.stream()
                        .map(outcome -> String.format("User %s has %d point", outcome.getPlayer().getFirstName(), outcome.getPoints().getAmount()))
                        .collect(Collectors.joining("\n"));

                final String resultBuilder = "Round results:" + "\n" + results;
                return new ProcessedResult(
                        new TextResponseMessage(resultBuilder, message.getChatId())
                );
            }


//            return new IterableResult(
//                    new TextResponseMessage(
//                            String.format("User %s has %d point", firstName, dice.getValue()),
//                            message.getChatId()
//                    )
//            );
        }

        return new SkipedResult();
    }

    @Override
    public Phase getPhase() {
        return Phase.STARTED;
    }

}
