package org.cubegame.application.handler;

import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.properties.ApplicationProperties;

import java.util.List;

public interface EventHandler {
    List<ResponseMessage> handle(Message message, ApplicationProperties properties);
}
