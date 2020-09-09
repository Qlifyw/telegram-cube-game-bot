package org.cubegame.domain.model.message.speach;

public final class Comment implements Speech {

    private final String text;

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
}
