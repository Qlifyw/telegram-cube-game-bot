package org.cubegame.application.executors.factory;

import org.cubegame.application.model.result.PhaseResponse;
import org.cubegame.domain.model.game.state.Phase;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.model.message.type.ResponseMessage;

import java.util.Optional;

public interface PhaseExecutor {

    Optional<ResponseMessage> inception();

    PhaseResponse execute(Message message);

    Phase getPhase();
}
