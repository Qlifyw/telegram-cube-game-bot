package org.cubegame.application.handler;

import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.ApplicationProperties;
import org.cubegame.infrastructure.TelegramBotView;

public interface EventHandler {
    void handle(Message message, TelegramBotView view, ApplicationProperties properties);
}
