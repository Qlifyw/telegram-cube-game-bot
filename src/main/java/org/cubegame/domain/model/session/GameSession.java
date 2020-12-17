package org.cubegame.domain.model.session;

import org.cubegame.domain.model.identifier.GameId;
import org.cubegame.domain.model.round.Outcomes;
import org.cubegame.domain.model.round.Round;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class GameSession {
    private final Deque<Round> rounds = new ArrayDeque<>();
    private final GameId relatedGame;

    public GameSession(final GameId gameId) {
        this.relatedGame = gameId;
        this.rounds.push(new Round(new Outcomes(), relatedGame));
    }

    public Round getActiveRound() {
        return this.rounds.peek();
    }

    public Round completeActiveRound() {
        final Outcomes nextRoundOutcomes = new Outcomes();
        final Round nextRound = new Round(nextRoundOutcomes, relatedGame);
        this.rounds.push(nextRound);
        return nextRound;
    }

    public List<Round> getAllRounds() {
        return new ArrayList<>(rounds);
    }
}
