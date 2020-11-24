package org.cubegame.application.executors.phase;

import org.cubegame.application.exceptions.incident.Incident;
import org.cubegame.application.exceptions.incident.internal.Internal;
import org.cubegame.application.exceptions.incident.internal.InternalError;
import org.cubegame.application.executors.factory.PhaseExecutor;
import org.cubegame.application.model.result.FailedResult;
import org.cubegame.application.model.result.PhaseResponse;
import org.cubegame.application.model.result.ProcessedResult;
import org.cubegame.application.model.result.SkipedResult;
import org.cubegame.application.repositories.game.GameRepository;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.game.GameBuilder;
import org.cubegame.domain.model.game.state.Phase;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.model.message.ErrorResponseMessage;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.TextResponseMessage;

import java.util.Collections;
import java.util.Optional;

public class PlayersAmountPhaseExecutor implements PhaseExecutor {

    private final ChatId chatId;
    private final GameRepository gameRepository;

    public PlayersAmountPhaseExecutor(final ChatId chatId, final GameRepository gameRepository) {
        this.gameRepository = gameRepository;
        this.chatId = chatId;
    }

    @Override
    public Optional<ResponseMessage> inception() {
        final TextResponseMessage initMessage = new TextResponseMessage("Specify players amount", chatId);
        return Optional.of(initMessage);
    }

    @Override
    public PhaseResponse execute(Message message) {
        switch (message.getSpeech().getType()) {
            case COMMENT:
                return new SkipedResult();
            case APEAL:
                break;
        }

        final Game storedGame = gameRepository
                .getActive(message.getChatId())
                .orElseThrow(this::gameNotFoundException);

        if (!message.getAuthor().getUserId().equals(storedGame.getOwner()))
            return new SkipedResult();

        final int numberOfPlayers;
        try {
            numberOfPlayers = Integer.parseInt(message.getSpeech().getText());
        } catch (NumberFormatException exception) {
            return new FailedResult(
                    new ErrorResponseMessage(
                            "Invalid number of players. Please enter integer value.",
                            message.getChatId()
                    )
            );
        }
        if (numberOfPlayers <= 0) {
            return new FailedResult(
                    new ErrorResponseMessage(
                            "Invalid number of players. Number must be greater than 0.",
                            message.getChatId()
                    )
            );
        }

        final Phase nextPhase = Phase.getNextFor(getPhase());

        final Game updatedGame = GameBuilder.from(storedGame)
                .setPhase(nextPhase)
                .setNumberOfPlayers(numberOfPlayers)
                .build();
        gameRepository.update(updatedGame);

        return new ProcessedResult(
                new TextResponseMessage(
                        String.format("Await for %d players", updatedGame.getNumberOfPlayers()),
                        message.getChatId()
                )
        );
    }

    @Override
    public Phase getPhase() {
        return Phase.NUMBER_OF_PLAYERS;
    }

    private Incident gameNotFoundException() {
        return new InternalError(
                Internal.Logical.INCONSISTENCY,
                String.format("Cannot find game session for chat with id '%d'", chatId.getValue()),
                Collections.emptyMap(),
                null
        );
    }
}
