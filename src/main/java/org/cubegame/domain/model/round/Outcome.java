package org.cubegame.domain.model.round;

import org.cubegame.domain.model.identifier.UserId;

public class Outcome {
    private final UserId userId;
    private final Points points;

    public Outcome(final UserId userId, final Points points) {
        this.userId = userId;
        this.points = points;
    }

    public UserId getUserId() {
        return userId;
    }

    public Points getPoints() {
        return points;
    }
}
