package org.cubegame.application.handler;

import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.model.message.type.ResponseMessage;

import java.util.List;

public interface EventHandler {
    List<ResponseMessage> handle(Message message);
}
