package org.cubegame.infrastructure.repositories.round.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import org.cubegame.domain.model.game.Player;
import org.cubegame.domain.model.identifier.UserId;

public class PlayerEntity {

    @JsonValue
    private final long userId;

    public PlayerEntity(final long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public static PlayerEntity fromDomain(Player player) {
        return new PlayerEntity(player.getUserId().getValue());
    }

    public static Player toDomain(PlayerEntity playerEntity) {
        final UserId userId = new UserId(playerEntity.getUserId());
        return new Player(userId, "");
    }
}
