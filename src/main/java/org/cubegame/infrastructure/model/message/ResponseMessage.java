package org.cubegame.infrastructure.model.message;

import org.cubegame.domain.model.identifier.ChatId;

public abstract class ResponseMessage {
    protected String message;
    protected ChatId chatId;

    public ResponseMessage(final String message, final ChatId chatId) {
        this.message = message;
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public ChatId getChatId() {
        return chatId;
    }
}
