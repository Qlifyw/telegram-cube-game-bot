package org.cubegame.application.handler;

import org.cubegame.application.model.FailedResult;
import org.cubegame.application.model.PhaseStatebleResponse;
import org.cubegame.application.model.ProceduralResult;
import org.cubegame.application.model.SkipedResult;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.exceptions.GameNoFoundException;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.game.GameBuilder;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.model.message.ErrorResponseMessage;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.TextResponseMessage;
import org.cubegame.infrastructure.repository.game.GameRepository;

import java.util.Optional;

public class RoundAmountPhaseExecutor implements PhaseExecutor {

    private final ChatId chatId;
    private final GameRepository gameRepository;

    public RoundAmountPhaseExecutor(final ChatId chatId, final GameRepository gameRepository) {
        this.gameRepository = gameRepository;
        this.chatId = chatId;
    }

    @Override
    public Optional<ResponseMessage> initiation() {
        final TextResponseMessage initMessage = new TextResponseMessage("Specify rounds amount for win", chatId);
        return Optional.of(initMessage);
    }

    @Override
    public PhaseStatebleResponse execute(Message message) {
        switch (message.getSpeech().getType()) {
            case COMMENT:
                return new SkipedResult();
            case APEAL:
                break;
        }

        final Game storedGame = gameRepository
                .get(message.getChatId())
                .orElseThrow(() -> new GameNoFoundException(message.getChatId()));

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
        gameRepository.save(updatedGame);

        return new ProceduralResult();
    }

    @Override
    public Phase getPhase() {
        return Phase.NUMBER_OF_ROUNDS;
    }
}
