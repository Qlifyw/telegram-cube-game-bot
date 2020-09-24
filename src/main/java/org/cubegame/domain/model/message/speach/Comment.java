package org.cubegame.domain.model.message.speach;

public final class Comment implements Speech {

    private final String text;

    // TODO make it private
    public Comment(final String text) {
        this.text = text;
    }

    @Override
    public SpeaehType getType() {
        return SpeaehType.COMMENT;
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
