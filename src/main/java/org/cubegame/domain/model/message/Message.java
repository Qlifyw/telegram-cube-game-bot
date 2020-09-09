package org.cubegame.domain.model.message;

import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.UserId;
import org.cubegame.domain.model.message.speach.Speech;
import org.telegram.telegrambots.meta.api.objects.Dice;

public class Message {

    private final ChatId chatId;
    private final Speech speech;
    private final Dice dice;
    private final Author author;

    public Message(ChatId chatId, UserId userId, String firstName, Speech speech, Dice dice) {
        this.chatId = chatId;
        this.speech = speech;
        this.dice = dice;
        this.author = new Author(userId, firstName);
    }

    public ChatId getChatId() {
        return chatId;
    }

    public Speech getSpeech() {
        return speech;
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
