package org.cubegame.domain.model.round;

import org.cubegame.domain.model.game.Player;

import java.util.List;
import java.util.UUID;

public class RoundOutcomes {
    private final UUID id;
    private final Player winner;
    private final List<Outcome> results;

    public RoundOutcomes(final UUID id, final Player winner, final List<Outcome> results) {
        this.id = id;
        this.winner = winner;
        this.results = results;
    }

    public UUID getId() {
        return id;
    }

    public List<Outcome> getResults() {
        return results;
    }

    public Player getWinner() {
        return winner;
    }
}
