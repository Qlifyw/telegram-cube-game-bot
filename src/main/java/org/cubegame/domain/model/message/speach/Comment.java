package org.cubegame.domain.model.message.speach;

final class Comment implements Speech {

    private final String text;

    Comment(final String text) {
        this.text = text;
    }

    @Override
    public SpeechType getType() {
        return SpeechType.COMMENT;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
