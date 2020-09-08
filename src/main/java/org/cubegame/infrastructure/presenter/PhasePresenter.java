package org.cubegame.infrastructure.presenter;

import org.cubegame.domain.events.Phase;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.infrastructure.model.message.NavigationResponseMessage;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.TextResponseMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.List;

public class PhasePresenter {

    private PhasePresenter() {
    }



    private static InlineKeyboardMarkup buildMenu() {
        final InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton("cube");
        inlineKeyboardButton1.setCallbackData("cube-game");
        final InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton("darts");
        inlineKeyboardButton2.setUrl("https://google.com");

        final List<InlineKeyboardButton> buttons = Arrays.asList(inlineKeyboardButton1, inlineKeyboardButton2);
        InlineKeyboardMarkup menu = new InlineKeyboardMarkup(Arrays.asList(buttons));
        return menu;
    }
}
