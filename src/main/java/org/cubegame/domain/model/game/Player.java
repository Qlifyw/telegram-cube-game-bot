package org.cubegame.domain.model.game;

import org.cubegame.domain.model.identifier.UserId;

import java.util.Objects;

public class Player {

    private final UserId userId;
    private final String firstName;

    public Player(final UserId userId, final String firstName) {
        this.userId = userId;
        this.firstName = firstName;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    @Override
    public String toString() {
        return "Player{" +
                "userId=" + userId +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Player player = (Player) o;
        return Objects.equals(userId, player.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
