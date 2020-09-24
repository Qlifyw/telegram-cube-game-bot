package org.cubegame.application.handler;

import org.cubegame.domain.events.CommandValidator;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.infrastructure.properties.ApplicationProperties;
import org.cubegame.infrastructure.repository.game.GameRepository;

public class PhaseExecutorFactory {

    private final GameRepository gameRepository;

//    private static Map<Phase, PhaseExecutor> executors = new LinkedHashMap<>();

    PhaseExecutorFactory(final GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public PhaseExecutor newInstance(Phase phase, ChatId chatId) {
//        final PhaseExecutor executor = executors.get(phase);
//        if (executor == null) {
//            final PhaseExecutor newExecutor = createExecutor(phase, chatId);
//            executors.put(phase, newExecutor);
//            return newExecutor;
//        }
        return createExecutor(phase, chatId);
    }

    private PhaseExecutor createExecutor(Phase phase, ChatId chatId) {
        PhaseExecutor executor = null;
        switch (phase) {
            case EMPTY:
                final CommandValidator commandValidator = new CommandValidator(ApplicationProperties.load());
                executor = new EmptyPhaseExecutor(chatId, commandValidator, gameRepository);
                break;
            case CHOOSE_GAME:
                executor = new ChooseGamePhaseExecutor(chatId, gameRepository);
                break;
            case NUMBER_OF_PLAYERS:
                executor = new PlayersAmountPhaseExecutor(chatId, gameRepository);
                break;
            case AWAIT_PLAYERS:
                executor = new PlayersAwaitingPhaseExecutor(chatId, gameRepository);
                break;
            case STARTED:
                executor = new StartGamePhaseExecutor(chatId, gameRepository);
                break;
            case COMPLETED:
                executor = new CompleteGamePhaseExecutor();
                break;
        }
        return executor;
    }

}
