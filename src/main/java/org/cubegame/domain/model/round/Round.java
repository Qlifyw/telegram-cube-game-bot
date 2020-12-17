package org.cubegame.domain.model.round;

import org.cubegame.domain.model.game.Player;
import org.cubegame.domain.model.identifier.GameId;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

public class Round {
    private final GameId relatedGame;
    private final UUID id;
    private final Outcomes results;

    public Round(final Outcomes results, final GameId relatedGame) {
        this.id = UUID.randomUUID();
        this.relatedGame = relatedGame;
        this.results = results;
    }

    public UUID getId() {
        return id;
    }

    public Outcomes getResults() {
        return results;
    }

    public GameId getRelatedGame() {
        return relatedGame;
    }

    public Optional<Player> getWinner() {
        return results.stream()
                .max(Comparator.comparing(Outcome::getPoints))
                .map(Outcome::getPlayer);
    }
}
