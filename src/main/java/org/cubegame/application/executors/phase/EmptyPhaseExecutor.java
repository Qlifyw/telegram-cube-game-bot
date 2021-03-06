package org.cubegame.application.executors.phase;

import org.cubegame.application.executors.factory.PhaseExecutor;
import org.cubegame.application.model.result.FailedResult;
import org.cubegame.application.model.result.PhaseResponse;
import org.cubegame.application.model.result.ProceduralResult;
import org.cubegame.application.model.result.SkipedResult;
import org.cubegame.application.repositories.game.GameRepository;
import org.cubegame.domain.events.Command;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.game.GameBuilder;
import org.cubegame.domain.model.game.state.Phase;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.model.message.TextResponseMessage;
import org.cubegame.infrastructure.model.message.type.ResponseMessage;
import org.cubegame.infrastructure.services.CommandValidator;

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
    public Optional<ResponseMessage> inception() {
        return Optional.empty();
    }

    @Override
    public PhaseResponse execute(Message message) {

        final String unvalidatedCommand = message.getSpeech().getText();

        final Optional<CommandValidator.ValidatedCommand> validatedCommand = commandValidator.validate(unvalidatedCommand);

        if (!validatedCommand.isPresent())
            return new SkipedResult();

        switch (validatedCommand.get().getValue()) {
            case START:
                final Phase nextPhase = Phase.getNextFor(getPhase());

                final Game createdGame = new GameBuilder()
                        .setChatId(chatId)
                        .setOwner(message.getAuthor().getUserId())
                        .setPhase(nextPhase)
                        .build();

                gameRepository.save(createdGame);
                return new ProceduralResult();

            case STOP:
                final TextResponseMessage failDescription = new TextResponseMessage(
                        String.format("Command %s is not implemented yet.", Command.STOP),
                        message.getChatId()
                );
                return new FailedResult(failDescription);
        }

        return new SkipedResult();
    }

    @Override
    public Phase getPhase() {
        return Phase.EMPTY;
    }

}
