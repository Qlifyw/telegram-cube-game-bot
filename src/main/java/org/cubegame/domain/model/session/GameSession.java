package org.cubegame.domain.model.session;

import org.cubegame.domain.model.round.Outcomes;
import org.cubegame.domain.model.round.Round;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameSession {
    private final Stack<Round> rounds = new Stack<>();

    public GameSession() {
        this.rounds.push(new Round(new Outcomes()));
    }

    public Round getActiveRound() {
        return this.rounds.peek();
    }

    public Round completeActiveRound() {
        final Outcomes nextRoundOutcomes = new Outcomes();
        final Round nextRound = new Round(nextRoundOutcomes);
        this.rounds.push(nextRound);
        return nextRound;
    }

    public List<Round> getAllRounds() {
        return new ArrayList<>(rounds);
    }
}
