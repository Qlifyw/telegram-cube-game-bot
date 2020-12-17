package org.cubegame.domain.model.message;

import org.cubegame.domain.model.dice.Dice;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.MessageId;
import org.cubegame.domain.model.identifier.UserId;
import org.cubegame.domain.model.message.speach.Speech;

public class Message {

    private final ChatId chatId;
    private final Speech speech;
    private final Dice dice;
    private final Author author;
    private final MessageId forwardedMessageId;

    public Message(ChatId chatId, UserId userId, String firstName, Speech speech, Dice dice, MessageId forwardedMessageId) {
        this.chatId = chatId;
        this.speech = speech;
        this.dice = dice;
        this.author = new Author(userId, firstName);
        this.forwardedMessageId = forwardedMessageId;
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

    public MessageId getForwardedMessageId() {
        return forwardedMessageId;
    }

    public boolean isForwarded() {
        return forwardedMessageId != null;
    }

    public Author getAuthor() {
        return author;
    }
}
