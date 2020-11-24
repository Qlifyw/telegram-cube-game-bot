package org.cubegame.application.executors.phase;

import org.cubegame.application.executors.factory.PhaseExecutor;
import org.cubegame.application.model.result.PhaseResponse;
import org.cubegame.application.model.result.SkipedResult;
import org.cubegame.domain.model.game.state.Phase;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.model.message.ResponseMessage;

import java.util.Optional;

public class CompleteGamePhaseExecutor implements PhaseExecutor {

    @Override
    public Optional<ResponseMessage> inception() {
        return Optional.empty();
    }

    @Override
    public PhaseResponse execute(Message message) {
        return new SkipedResult();
    }

    @Override
    public Phase getPhase() {
        return Phase.COMPLETED;
    }

}
