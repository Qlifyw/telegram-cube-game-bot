package org.cubegame.domain.model;

public class Message {

    private final ChatId chatId;
    private final UserId userId;
    private final String message;

    public Message(ChatId chatId, UserId userId, String message) {
        this.chatId = chatId;
        this.userId = userId;
        this.message = message;
    }

    public ChatId getChatId() {
        return chatId;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }
}
