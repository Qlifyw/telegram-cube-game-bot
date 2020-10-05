package org.cubegame.application.handler;

import org.cubegame.application.model.PhaseStatebleResponse;
import org.cubegame.application.model.SkipedResult;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.model.message.ResponseMessage;

import java.util.Optional;

public class CompleteGamePhaseExecutor implements PhaseExecutor {

    @Override
    public Optional<ResponseMessage> initiation() {
        return Optional.empty();
    }

    @Override
    public PhaseStatebleResponse execute(Message message) {
        return new SkipedResult();
    }

    @Override
    public Phase getPhase() {
        return Phase.COMPLETED;
    }

}
