package org.cubegame.infrastructure.model.message.type;

import org.cubegame.domain.model.identifier.ChatId;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class NavigationResponseMessage extends ResponseMessage {

    private InlineKeyboardMarkup menu;

    public NavigationResponseMessage(final InlineKeyboardMarkup menu, final ChatId chatId) {
        super("", chatId, ResponseType.NAVIAGTION);
        this.menu = menu;
    }

    public InlineKeyboardMarkup getMenu() {
        return menu;
    }
}
