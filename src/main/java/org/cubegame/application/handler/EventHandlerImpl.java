package org.cubegame.application.handler;

import org.cubegame.application.model.FailedResult;
import org.cubegame.application.model.IterableResult;
import org.cubegame.application.model.PhaseResponse;
import org.cubegame.application.model.ProcessedResult;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.properties.ApplicationProperties;
import org.cubegame.infrastructure.repository.game.GameRepository;
import org.cubegame.infrastructure.repository.round.RoundRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class EventHandlerImpl implements EventHandler {

    private final GameRepository gameRepository;
    private final PhaseExecutorFactory phaseExecutorFactory;

    private final Map<ChatId, PhaseExecutor> phaseExecutors = new ConcurrentHashMap<>();

    public EventHandlerImpl(
            final GameRepository gameRepository,
            final RoundRepository roundRepository,
            final ApplicationProperties properties
    ) {
        this.gameRepository = gameRepository;
        this.phaseExecutorFactory = new PhaseExecutorFactory(gameRepository, roundRepository, properties);
    }

    @Override
    public List<ResponseMessage> handle(Message receivedMessage) {

        final List<ResponseMessage> responses = new ArrayList<>();

        final Optional<Game> game = gameRepository.getActive(receivedMessage.getChatId());

        final Phase phase = game
                .flatMap(storedGame -> Optional.of(storedGame.getPhase()))
                .orElse(Phase.EMPTY);

        final PhaseExecutor executor;
        if (phaseExecutors.get(receivedMessage.getChatId()) == null) {
            final PhaseExecutor newExecutor = phaseExecutorFactory.newInstance(phase, receivedMessage.getChatId());
            phaseExecutors.put(receivedMessage.getChatId(), newExecutor);
            executor = newExecutor;
        } else {
            executor = phaseExecutors.get(receivedMessage.getChatId());
        }

        final PhaseResponse processingResult = executor.execute(receivedMessage);

        switch (processingResult.getStatus()) {
            case PROCESSED: {
                final ResponseMessage responseMessage = ((ProcessedResult) processingResult).getResponseMessage();
                responses.add(responseMessage);
                phaseExecutors.remove(receivedMessage.getChatId());

                final PhaseExecutor nextExecutor = phaseExecutorFactory.newInstance(Phase.getNextFor(phase), receivedMessage.getChatId());
                if (nextExecutor != null) {
                    nextExecutor
                            .initiation()
                            .ifPresent(responses::add);
                    phaseExecutors.put(receivedMessage.getChatId(), nextExecutor);
                }
                break;
            }

            case FAILED:
                final ResponseMessage fail = ((FailedResult) processingResult).getResponseMessage();
                responses.add(fail);
                break;

            case PROCEDURAL: {
                phaseExecutors.remove(receivedMessage.getChatId());
                final PhaseExecutor nextExecutor = phaseExecutorFactory.newInstance(Phase.getNextFor(phase), receivedMessage.getChatId());
                nextExecutor
                        .initiation()
                        .ifPresent(responses::add);
                phaseExecutors.put(receivedMessage.getChatId(), nextExecutor);
                break;
            }

            case ITERABLE:
                final ResponseMessage iteration = ((IterableResult) processingResult).getResponseMessage();
                responses.add(iteration);
                break;

            case SKIPPED:
                break;
        }

        return responses;
    }

}
