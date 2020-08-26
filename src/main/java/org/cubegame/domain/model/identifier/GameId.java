package org.cubegame.domain.model.identifier;

import java.util.UUID;

public class GameId {
    private final UUID value;

    public GameId(UUID value) {
        this.value = value;
    }

    public UUID getValue() {
        return value;
    }
}
