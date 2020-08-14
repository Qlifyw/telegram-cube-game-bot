package org.cubegame.infrastructure.model.message;

import org.cubegame.domain.model.ChatId;

public class TextResponseMessage extends ResponseMessage {

    public TextResponseMessage(final String value, final ChatId chatId) {
        super(value, chatId);
    }
}
