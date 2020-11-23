package org.cubegame.application.executors.factory;

import org.cubegame.application.exceptions.incident.Incident;
import org.cubegame.application.exceptions.incident.internal.Internal;
import org.cubegame.application.exceptions.incident.internal.InternalError;
import org.cubegame.application.executors.phase.ChooseGamePhaseExecutor;
import org.cubegame.application.executors.phase.EmptyPhaseExecutor;
import org.cubegame.application.executors.phase.PlayersAmountPhaseExecutor;
import org.cubegame.application.executors.phase.PlayersAwaitingPhaseExecutor;
import org.cubegame.application.executors.phase.RoundAmountPhaseExecutor;
import org.cubegame.application.executors.phase.StartGamePhaseExecutor;
import org.cubegame.application.repositories.game.GameRepository;
import org.cubegame.application.repositories.round.RoundRepository;
import org.cubegame.domain.model.game.state.Phase;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.infrastructure.services.CommandValidator;

import java.util.Collections;
import java.util.Optional;

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
        return createExecutor(phase, chatId)
                .orElseThrow(() -> cannotCreateExecutorException(phase) );
    }

    private Incident cannotCreateExecutorException(Phase phase) {
        return new InternalError(
                Internal.Logical.INCONSISTENCY,
                "Cannot create executor for '" +phase+ "'.",
                Collections.emptyMap(),
                null
        );
    }

    private Optional<PhaseExecutor> createExecutor(Phase phase, ChatId chatId) {
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

        return executor == null ? Optional.empty() : Optional.of(executor);
    }

}
