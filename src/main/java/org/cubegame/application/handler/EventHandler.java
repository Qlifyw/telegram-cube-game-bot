package org.cubegame.application.handler;

import org.cubegame.infrastructure.ApplicationProperties;
import org.cubegame.infrastructure.TelegramBotView;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface EventHandler {
    void handle(Update update, TelegramBotView view, ApplicationProperties properties);
}
