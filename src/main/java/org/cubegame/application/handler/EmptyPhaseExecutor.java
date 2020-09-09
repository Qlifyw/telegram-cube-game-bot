package org.cubegame.application.handler;

import org.cubegame.application.model.FailedResult;
import org.cubegame.application.model.PhaseStatusable;
import org.cubegame.application.model.ProceduralResult;
import org.cubegame.application.model.SkipedResult;
import org.cubegame.domain.events.Command;
import org.cubegame.domain.events.CommandValidator;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.exceptions.EnumException;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.game.GameBuilder;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.model.message.ErrorResponseMessage;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.TextResponseMessage;
import org.cubegame.infrastructure.repository.game.GameRepository;

import java.util.Optional;

public class EmptyPhaseExecutor implements PhaseExecutor {

    @Override
    public Optional<ResponseMessage> initiation(final ChatId chatId) {
        return Optional.empty();
    }

    @Override
    public PhaseStatusable execute(Message message, GameRepository gameRepository) {

        switch (message.getSpeech().getType()) {
            case COMMENT:
                return new SkipedResult();
            case APEAL:
                break;
        }


//        final Optional<UnvalidatedCommand> maybeCommand = UnvalidatedCommand.from(message.getSpeech());

//        if (maybeCommand.isPresent()) {

            final String unvalidatedCommand = message.getSpeech().getText();

            final CommandValidator.ValidatedCommand validatedCommand;
            try {
                validatedCommand = CommandValidator.validateOrThrow(unvalidatedCommand);
            } catch (EnumException exception) {
                // TODO log it
                System.out.println(exception.toString());
                return new FailedResult(invalidCommand(unvalidatedCommand, message.getChatId()));
            }

            switch (validatedCommand.getValue()) {
                case START:
                    final Phase nextPhase = Phase.getNextFor(getPhase());

                    final ChatId chatId = message.getChatId();
                    final Game createdGame = new GameBuilder()
                            .setChatId(chatId)
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
//        }

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
