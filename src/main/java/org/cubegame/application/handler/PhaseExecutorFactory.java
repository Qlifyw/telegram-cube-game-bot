package org.cubegame.application.handler;

import org.cubegame.domain.events.CommandValidator;
import org.cubegame.domain.events.Phase;
import org.cubegame.infrastructure.properties.ApplicationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

public class PhaseExecutorFactory {

    private static Map<Phase, PhaseExecutor> executors = new LinkedHashMap<>();

    private PhaseExecutorFactory() {
    }

    public static PhaseExecutor of(Phase phase) {
        final PhaseExecutor executor = executors.get(phase);
        if (executor == null) {
            final PhaseExecutor newExecutor = createExecutor(phase);
            executors.put(phase, newExecutor);
            return newExecutor;
        }
        return executor;
    }

    private static PhaseExecutor createExecutor(Phase phase) {
        PhaseExecutor executor = null;
        switch (phase) {
            case EMPTY:
                final CommandValidator commandValidator = new CommandValidator(ApplicationProperties.load());
                executor = new EmptyPhaseExecutor(commandValidator);
                break;
            case CHOOSE_GAME:
                executor = new ChooseGamePhaseExecutor();
                break;
            case NUMBER_OF_PLAYERS:
                executor = new PlayersAmountPhaseExecutor();
                break;
            case AWAIT_PLAYERS:
                executor = new PlayersAwaitingPhaseExecutor();
                break;
            case STARTED:
                executor = new StartGamePhaseExecutor();
                break;
            case COMPLETED:
                break;
        }
        return executor;
    }

}
