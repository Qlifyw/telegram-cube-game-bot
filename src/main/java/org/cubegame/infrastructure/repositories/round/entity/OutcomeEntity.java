package org.cubegame.infrastructure.repositories.round.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cubegame.domain.model.round.Outcome;

public class OutcomeEntity {

    @JsonProperty("playerProp")
    private final PlayerEntity player;

    @JsonProperty("pointsProp")
    private final PointsEntity points;

    public OutcomeEntity(final PlayerEntity player, final PointsEntity points) {
        this.player = player;
        this.points = points;
    }


    public static OutcomeEntity fromDomain(Outcome outcome) {
        return new OutcomeEntity(
                PlayerEntity.fromDomain(outcome.getPlayer()),
                PointsEntity.fromDomain(outcome.getPoints())
        );
    }
}
