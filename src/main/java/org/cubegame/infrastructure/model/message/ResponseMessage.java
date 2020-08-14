package org.cubegame.infrastructure.model.message;

import org.cubegame.domain.model.ChatId;

public abstract class ResponseMessage {
    protected String value;
    protected ChatId chatId;

    public ResponseMessage(final String value, final ChatId chatId) {
        this.value = value;
        this.chatId = chatId;
    }

    public String getValue() {
        return value;
    }

    public ChatId getChatId() {
        return chatId;
    }
}
