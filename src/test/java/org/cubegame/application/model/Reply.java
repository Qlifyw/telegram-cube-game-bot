package org.cubegame.application.model;

import org.cubegame.domain.model.identifier.UserId;
import org.cubegame.domain.model.message.speach.Speech;

public class Reply {
    private final UserId userId;
    private final Speech speech;

    public Reply(final UserId userId, final Speech speech) {
        this.userId = userId;
        this.speech = speech;
    }

    public UserId getUserId() {
        return userId;
    }

    public Speech getSpeech() {
        return speech;
    }
}
