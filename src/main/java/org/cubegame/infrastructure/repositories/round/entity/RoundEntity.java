package org.cubegame.infrastructure.repositories.round.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cubegame.domain.model.round.Round;

import java.util.List;
import java.util.stream.Collectors;

public class RoundEntity {

    @JsonProperty("roundId")
    private final String id;

    @JsonProperty("relatedGame")
    private final String relatedGame;

    @JsonProperty("outcomes")
    private final List<OutcomeEntity> results;

    @JsonCreator
    public RoundEntity(
            @JsonProperty("roundId") final List<OutcomeEntity> results,
            @JsonProperty("relatedGame") final String relatedGame,
            @JsonProperty("outcomes") final String id
    ) {
        this.id = id;
        this.relatedGame = relatedGame;
        this.results = results;
    }

    public static RoundEntity fromDomain(Round round) {
        return new RoundEntity(
                round.getResults()
                        .stream()
                        .map(OutcomeEntity::fromDomain)
                        .collect(Collectors.toList()),
                round.getRelatedGame().toString(),
                round.getId().toString()
        );
    }

}
