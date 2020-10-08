package org.cubegame.application.handler;

import org.cubegame.application.model.PhaseResponse;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.model.message.ResponseMessage;

import java.util.Optional;

public interface PhaseExecutor {

    Optional<ResponseMessage> initiation();

    PhaseResponse execute(Message message);

    Phase getPhase();
}
