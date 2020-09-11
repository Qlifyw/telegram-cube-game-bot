package org.cubegame.domain.model.round;

import java.util.List;
import java.util.UUID;

public class Round {
    private final UUID id;
    private final List<Outcome> results;

    public Round(final UUID id, final List<Outcome> results) {
        this.id = id;
        this.results = results;
    }

    public UUID getId() {
        return id;
    }

    public List<Outcome> getResults() {
        return results;
    }
}
