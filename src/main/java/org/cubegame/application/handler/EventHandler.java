package org.cubegame.application.handler;

import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.ApplicationProperties;
import org.cubegame.infrastructure.model.message.ResponseMessage;

public interface EventHandler {
    ResponseMessage handle(Message message, ApplicationProperties properties);
}
