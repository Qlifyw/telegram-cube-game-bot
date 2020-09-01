package org.cubegame.infrastructure.model.message;

import org.cubegame.domain.model.identifier.ChatId;

public abstract class TextualResponseMessage extends ResponseMessage {

    public TextualResponseMessage(final String message, final ChatId chatId, final ResponseType type) {
        super(message, chatId, type);
    }

}
