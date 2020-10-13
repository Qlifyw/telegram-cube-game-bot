package org.cubegame.application.executors.factory;

import org.cubegame.application.executors.phase.ChooseGamePhaseExecutor;
import org.cubegame.application.executors.phase.EmptyPhaseExecutor;
import org.cubegame.application.executors.phase.PlayersAmountPhaseExecutor;
import org.cubegame.application.executors.phase.PlayersAwaitingPhaseExecutor;
import org.cubegame.application.executors.phase.RoundAmountPhaseExecutor;
import org.cubegame.application.executors.phase.StartGamePhaseExecutor;
import org.cubegame.domain.model.game.state.Phase;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.infrastructure.repositories.game.GameRepository;
import org.cubegame.infrastructure.repositories.round.RoundRepository;
import org.cubegame.infrastructure.services.CommandValidator;

public class PhaseExecutorFactory {

    private final GameRepository gameRepository;
    private final RoundRepository roundRepository;
    private final CommandValidator commandValidator;

    public PhaseExecutorFactory(
            final GameRepository gameRepository,
            final RoundRepository roundRepository,
            final CommandValidator commandValidator
    ) {
        this.gameRepository = gameRepository;
        this.roundRepository = roundRepository;
        this.commandValidator = commandValidator;
    }

    public PhaseExecutor newInstance(Phase phase, ChatId chatId) {
        return createExecutor(phase, chatId);
    }

    private PhaseExecutor createExecutor(Phase phase, ChatId chatId) {
        PhaseExecutor executor = null;
        switch (phase) {
            case EMPTY:
                executor = new EmptyPhaseExecutor(chatId, commandValidator, gameRepository);
                break;
            case CHOOSE_GAME:
                executor = new ChooseGamePhaseExecutor(chatId, gameRepository);
                break;
            case NUMBER_OF_PLAYERS:
                executor = new PlayersAmountPhaseExecutor(chatId, gameRepository);
                break;
            case NUMBER_OF_ROUNDS:
                executor = new RoundAmountPhaseExecutor(chatId, gameRepository);
                break;
            case AWAIT_PLAYERS:
                executor = new PlayersAwaitingPhaseExecutor(chatId, gameRepository);
                break;
            case STARTED:
                executor = new StartGamePhaseExecutor(chatId, gameRepository, roundRepository);
                break;
            case CANCELED:
            case COMPLETED:
                break;
        }
        return executor;
    }

}
