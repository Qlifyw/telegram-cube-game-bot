package org.cubegame.domain.model.identifier;

import java.util.UUID;

public final class GameId {
    private final UUID value;

    public GameId(UUID value) {
        this.value = value;
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue().toString();
    }
}
