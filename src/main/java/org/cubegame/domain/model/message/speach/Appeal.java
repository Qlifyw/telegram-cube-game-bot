package org.cubegame.domain.model.message.speach;

public final class Appeal implements Speech {

    private final String text;

    // TODO make it private
    public Appeal(final String text) {
        this.text = text;
    }

    @Override
    public SpeaehType getType() {
        return SpeaehType.APEAL;
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
