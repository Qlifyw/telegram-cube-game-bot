package org.cubegame.infrastructure.model.message;

import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.infrastructure.model.message.type.ResponseType;
import org.cubegame.infrastructure.model.message.type.TextualResponseMessage;

public class ErrorResponseMessage extends TextualResponseMessage {

    public ErrorResponseMessage(final String value, final ChatId chatId) {
        super(value, chatId, ResponseType.TEXT);
    }
}
