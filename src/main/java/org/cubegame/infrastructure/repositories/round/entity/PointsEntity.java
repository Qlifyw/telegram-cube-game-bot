package org.cubegame.infrastructure.repositories.round.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import org.cubegame.domain.model.round.Points;

public class PointsEntity {

    @JsonValue
    private final int amount;

    public PointsEntity(final int amount) {
        this.amount = amount;
    }

    public static PointsEntity fromDomain(Points points) {
        return new PointsEntity(points.getAmount());
    }

}
