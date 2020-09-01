package org.cubegame.infrastructure.model.message;

import org.cubegame.domain.model.identifier.ChatId;

public class TextResponseMessage extends TextualResponseMessage {

    public TextResponseMessage(final String value, final ChatId chatId) {
        super(value, chatId, ResponseType.TEXT);
    }
}
