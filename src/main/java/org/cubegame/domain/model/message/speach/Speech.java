package org.cubegame.domain.model.message.speach;

public interface Speech {
    SpeechType getType();
    String getText();

    enum SpeechType {
        COMMENT,
        APEAL
    }
}
