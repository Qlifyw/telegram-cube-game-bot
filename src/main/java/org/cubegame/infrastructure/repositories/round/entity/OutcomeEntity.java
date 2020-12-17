package org.cubegame.infrastructure.repositories.round.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cubegame.domain.model.round.Outcome;

public class OutcomeEntity {

    @JsonProperty("player")
    private final PlayerEntity player;

    @JsonProperty("points")
    private final PointsEntity points;

    @JsonCreator
    public OutcomeEntity(
            @JsonProperty("player") final PlayerEntity player,
            @JsonProperty("points") final PointsEntity points
    ) {
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
