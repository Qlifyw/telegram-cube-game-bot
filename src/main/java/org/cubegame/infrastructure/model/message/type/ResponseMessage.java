package org.cubegame.infrastructure.model.message.type;

import org.cubegame.domain.model.identifier.ChatId;

public abstract class ResponseMessage {
    protected ResponseType type;
    protected String message;
    protected ChatId chatId;

    public ResponseMessage(final String message, final ChatId chatId, final ResponseType type) {
        this.message = message;
        this.chatId = chatId;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public ResponseType getType() {
        return type;
    }

    public ChatId getChatId() {
        return chatId;
    }
}
