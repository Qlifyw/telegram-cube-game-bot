package org.cubegame.infrastructure.model.message;

import org.cubegame.domain.model.ChatId;

public class ErrorResponseMessage extends ResponseMessage {

    public ErrorResponseMessage(final String value, final ChatId chatId) {
        super(value, chatId);
    }
}
