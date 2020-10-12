package org.cubegame.application.handler;

import org.cubegame.application.model.FailedResult;
import org.cubegame.application.model.IterableResult;
import org.cubegame.application.model.PhaseResponse;
import org.cubegame.application.model.ProcessedResult;
import org.cubegame.domain.events.Command;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.game.GameBuilder;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.properties.ApplicationProperties;
import org.cubegame.infrastructure.repositories.game.GameRepository;
import org.cubegame.infrastructure.repositories.round.RoundRepository;
import org.cubegame.infrastructure.services.CommandValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class EventHandlerImpl implements EventHandler {

    private final GameRepository gameRepository;
    private final PhaseExecutorFactory phaseExecutorFactory;
    private final CommandValidator commandValidator;

    private final Map<ChatId, PhaseExecutor> phaseExecutors = new ConcurrentHashMap<>();

    public EventHandlerImpl(
            final GameRepository gameRepository,
            final RoundRepository roundRepository,
            final ApplicationProperties properties
    ) {
        this.gameRepository = gameRepository;
        this.commandValidator = new CommandValidator(properties);
        this.phaseExecutorFactory = new PhaseExecutorFactory(gameRepository, roundRepository, commandValidator);
    }

    @Override
    public List<ResponseMessage> handle(Message receivedMessage) {
        final ChatId chatId = receivedMessage.getChatId();
        final Optional<Game> game = gameRepository.getActive(chatId);

        // Check is it command and process it if need
        final Optional<CommandValidator.ValidatedCommand> maybeCommand = commandValidator.validate(receivedMessage.getSpeech().getText());
        maybeCommand.ifPresent(command -> processCommand(command.getValue(), game.orElse(null)));

        final Phase phase = game
                .flatMap(storedGame -> Optional.of(storedGame.getPhase()))
                .orElse(Phase.EMPTY);    // if there is no stored game

        final PhaseExecutor executor = getExecutor(phase, chatId);
        final PhaseResponse processingResult = executor.execute(receivedMessage);
        final List<ResponseMessage> responses = processResponse(chatId, phase, processingResult);

        return responses;
    }

    private void cancelGame(Game game) {
        final GameBuilder builder = GameBuilder.from(game).setPhase(Phase.CANCELED);
        gameRepository.save(builder.build());
    }

    private void processCommand(Command command, Game game) {
        switch (command) {
            case START:
                break;
            case STOP:
                if (game != null) cancelGame(game);
                break;
        }
    }

    private PhaseExecutor getExecutor(Phase phase, ChatId chatId) {
        if (phaseExecutors.get(chatId) == null) {
            final PhaseExecutor newExecutor = phaseExecutorFactory.newInstance(phase, chatId);
            phaseExecutors.put(chatId, newExecutor);
            return newExecutor;
        } else {
            return phaseExecutors.get(chatId);
        }
    }

    private ResponseMessage handleProcessed(ChatId chatId, PhaseResponse processedResult) {
        final ResponseMessage responseMessage = ((ProcessedResult) processedResult).getResponseMessage();
        phaseExecutors.remove(chatId);
        return responseMessage;
    }

    private ResponseMessage handleFailed(PhaseResponse processedResult) {
        return ((FailedResult) processedResult).getResponseMessage();
    }

    private ResponseMessage handleIterable(PhaseResponse processedResult) {
        return ((IterableResult) processedResult).getResponseMessage();
    }

    private Optional<ResponseMessage> getNextPhaseEntrance(ChatId chatId, Phase phase) {
        final PhaseExecutor nextExecutor = phaseExecutorFactory.newInstance(Phase.getNextFor(phase), chatId);
        if (nextExecutor != null) {
            phaseExecutors.put(chatId, nextExecutor);
            return nextExecutor
                    .initiation();
        }
        return Optional.empty();
    }

    private List<ResponseMessage> processResponse(ChatId chatId, Phase phase, PhaseResponse processedResult) {
        final ArrayList<ResponseMessage> responses = new ArrayList<>();
        switch (processedResult.getStatus()) {
            case PROCESSED:
                responses.add(handleProcessed(chatId, processedResult));
                getNextPhaseEntrance(chatId, phase).ifPresent(responses::add);
                break;

            case FAILED:
                responses.add(handleFailed(processedResult));
                break;

            case PROCEDURAL:
                phaseExecutors.remove(chatId);
                getNextPhaseEntrance(chatId, phase).ifPresent(responses::add);
                break;

            case ITERABLE:
                responses.add(handleIterable(processedResult));
                break;

            case SKIPPED:
                break;
        }

        return responses;
    }

}
