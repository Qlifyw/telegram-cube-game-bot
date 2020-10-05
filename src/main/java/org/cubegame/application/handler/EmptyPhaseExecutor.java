package org.cubegame.application.handler;

import org.cubegame.application.model.FailedResult;
import org.cubegame.application.model.PhaseStatebleResponse;
import org.cubegame.application.model.ProceduralResult;
import org.cubegame.application.model.SkipedResult;
import org.cubegame.domain.events.Command;
import org.cubegame.domain.events.CommandValidator;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.game.GameBuilder;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.model.message.ErrorResponseMessage;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.TextResponseMessage;
import org.cubegame.infrastructure.repository.game.GameRepository;

import java.util.Optional;

public final class EmptyPhaseExecutor implements PhaseExecutor {

    private final ChatId chatId;
    private final CommandValidator commandValidator;
    private final GameRepository gameRepository;

    public EmptyPhaseExecutor(final ChatId chatId, final CommandValidator commandValidator, final GameRepository gameRepository) {
        this.commandValidator = commandValidator;
        this.gameRepository = gameRepository;
        this.chatId = chatId;
    }

    @Override
    public Optional<ResponseMessage> initiation() {
        return Optional.empty();
    }

    @Override
    public PhaseStatebleResponse execute(Message message) {

        final String unvalidatedCommand = message.getSpeech().getText();

        final Optional<CommandValidator.ValidatedCommand> validatedCommand = commandValidator
                .validate(unvalidatedCommand);

        if (!validatedCommand.isPresent())
            return new SkipedResult();

        switch (validatedCommand.get().getValue()) {
            case START:
                final Phase nextPhase = Phase.getNextFor(getPhase());

                final ChatId chatId = message.getChatId();
                final Game createdGame = new GameBuilder()
                        .setChatId(chatId)
                        .setOwner(message.getAuthor().getUserId())
                        .setPhase(nextPhase)
                        .build();

                gameRepository.save(createdGame);
                return new ProceduralResult();

            case STOP:
                return new FailedResult(
                        new TextResponseMessage(
                                String.format("Command %s is not implemented yet.", Command.STOP),
                                message.getChatId()
                        )
                );
        }

        return new SkipedResult();
    }

    @Override
    public Phase getPhase() {
        return Phase.EMPTY;
    }

    private ResponseMessage invalidCommand(String command, ChatId chatId) {
        return new ErrorResponseMessage(
                String.format("Invalid command: '%s'", command),
                chatId
        );
    }

}
