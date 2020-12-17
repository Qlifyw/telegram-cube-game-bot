package org.cubegame.domain.model.message.speach;

final class Appeal implements Speech {

    private final String text;

    Appeal(final String text) {
        this.text = text;
    }

    @Override
    public SpeechType getType() {
        return SpeechType.APEAL;
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
