package org.cubegame.infrastructure.repositories.round.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cubegame.domain.model.round.Round;

import java.util.List;
import java.util.stream.Collectors;

public class RoundEntity {

    @JsonProperty("roundIdProp")
    private final String id;

    @JsonProperty("relatedGameProp")
    private final String relatedGame;

    @JsonProperty("outcomesProp")
    private final List<OutcomeEntity> results;

    public RoundEntity(
            final List<OutcomeEntity> results,
            final String relatedGame,
            final String id
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
