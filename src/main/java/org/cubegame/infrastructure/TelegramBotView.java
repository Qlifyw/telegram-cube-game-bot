package org.cubegame.infrastructure;

import org.cubegame.domain.model.ChatId;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface TelegramBotView {

    void showMenu(InlineKeyboardMarkup menu, ChatId chatId);

    void respond(ResponseMessage message);
}
