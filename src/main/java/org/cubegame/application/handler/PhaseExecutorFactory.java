package org.cubegame.application.handler;

import org.cubegame.domain.events.CommandValidator;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.infrastructure.repository.game.GameRepository;
import org.cubegame.infrastructure.repository.round.RoundRepository;

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
            case COMPLETED:
//                executor = new CompleteGamePhaseExecutor();
                break;
        }
        return executor;
    }

}
