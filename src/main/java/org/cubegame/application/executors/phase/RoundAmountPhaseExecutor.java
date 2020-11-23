package org.cubegame.application.executors.phase;

import org.cubegame.application.exceptions.incident.Incident;
import org.cubegame.application.exceptions.incident.internal.Internal;
import org.cubegame.application.exceptions.incident.internal.InternalError;
import org.cubegame.application.executors.factory.PhaseExecutor;
import org.cubegame.application.model.FailedResult;
import org.cubegame.application.model.PhaseResponse;
import org.cubegame.application.model.ProceduralResult;
import org.cubegame.application.model.SkipedResult;
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

public class RoundAmountPhaseExecutor implements PhaseExecutor {

    private final ChatId chatId;
    private final GameRepository gameRepository;

    public RoundAmountPhaseExecutor(final ChatId chatId, final GameRepository gameRepository) {
        this.gameRepository = gameRepository;
        this.chatId = chatId;
    }

    @Override
    public Optional<ResponseMessage> inception() {
        final TextResponseMessage initMessage = new TextResponseMessage("Specify rounds amount for win", chatId);
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

        final int numberOfRounds;
        try {
            numberOfRounds = Integer.parseInt(message.getSpeech().getText());
        } catch (NumberFormatException exception) {
            return new FailedResult(
                    new ErrorResponseMessage(
                            "Invalid number of round. Please enter integer value.",
                            message.getChatId()
                    )
            );
        }
        if (numberOfRounds <= 0) {
            return new FailedResult(
                    new ErrorResponseMessage(
                            "Invalid number of round. Number must be greater than 0.",
                            message.getChatId()
                    )
            );
        }

        final Phase nextPhase = Phase.getNextFor(getPhase());

        final Game updatedGame = GameBuilder.from(storedGame)
                .setPhase(nextPhase)
                .setNumberOfRounds(numberOfRounds)
                .build();
        gameRepository.update(updatedGame);

        return new ProceduralResult();
    }

    @Override
    public Phase getPhase() {
        return Phase.NUMBER_OF_ROUNDS;
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
