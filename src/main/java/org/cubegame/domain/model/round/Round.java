package org.cubegame.domain.model.round;

import org.cubegame.domain.model.game.Player;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

public class Round {
    private final UUID id;
    private final Outcomes results;

    public Round(final Outcomes results) {
        this.id = UUID.randomUUID();
        this.results = results;
    }

    public UUID getId() {
        return id;
    }

    public Outcomes getResults() {
        return results;
    }

    public Optional<Player> getWinner() {
        return results.stream()
                .max(Comparator.comparing(Outcome::getPoints))
                .map(Outcome::getPlayer);
    }
}
