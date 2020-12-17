package org.cubegame.domain.model.round;

import org.cubegame.domain.model.game.Player;

public class Outcome {
    private final Player player;
    private final Points points;

    public Outcome(final Player player, final Points points) {
        this.player = player;
        this.points = points;
    }

    public Player getPlayer() {
        return player;
    }

    public Points getPoints() {
        return points;
    }
}
