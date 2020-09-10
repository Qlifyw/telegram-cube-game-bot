package org.cubegame.domain.model.game;

import org.cubegame.domain.model.identifier.UserId;

public class Player {

    private final UserId userId;

    public Player(final UserId userId) {
        this.userId = userId;
    }

    public UserId getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "Player{" +
                "userId=" + userId +
                '}';
    }
}
