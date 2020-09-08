package org.cubegame.domain.model.message;

import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.UserId;
import org.telegram.telegrambots.meta.api.objects.Dice;

public class Message {

    private final ChatId chatId;
    private final String message;
    private final Dice dice;
    private final Author author;

    public Message(ChatId chatId, UserId userId, String firstName, String message, Dice dice) {
        this.chatId = chatId;
        this.message = message;
        this.dice = dice;
        this.author = new Author(userId, firstName);
    }

    public ChatId getChatId() {
        return chatId;
    }

    public String getMessage() {
        return message;
    }

    public Dice getDice() {
        return dice;
    }

    public boolean hasDice() {
        return dice != null;
    }

    public Author getAuthor() {
        return author;
    }
}
