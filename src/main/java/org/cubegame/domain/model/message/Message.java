package org.cubegame.domain.model.message;

import org.cubegame.domain.events.Appeal;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.UserId;
import org.telegram.telegrambots.meta.api.objects.Dice;

public class Message {

    private final ChatId chatId;
    private final String text;
    private final Dice dice;
    private final Author author;

    public Message(ChatId chatId, UserId userId, String firstName, String text, Dice dice) {
        this.chatId = chatId;
        this.text = text;
        this.dice = dice;
        this.author = new Author(userId, firstName);
    }

    public Message apply(Appeal appeal) {
        return new Message(
                this.chatId,
                this.author.getUserId(),
                this.author.getFirstName(),
                appeal.toString(),
                this.dice
        );
    }

    public ChatId getChatId() {
        return chatId;
    }

    public String getText() {
        return text;
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
