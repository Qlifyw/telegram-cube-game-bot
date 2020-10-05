package org.cubegame.domain.model.game;

import org.cubegame.domain.events.Phase;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.GameId;
import org.cubegame.domain.model.identifier.UserId;

import java.util.Collections;
import java.util.List;

public class Game {

    private final GameId gameId;
    private final ChatId chatId;
    private final String gameName;
    private final int numberOfPlayers;
    private final int numberOfRounds;
    private final Phase phase;
    private final List<Player> players;
    private final UserId owner;


    public Game(
            final GameId gameId,
            final ChatId chatId,
            final Phase phase,
            final String gameName,
            int numberOfPlayers,
            final int numberOfRounds,
            final List<Player> players,
            final UserId owner
    ) {
        this.gameId = gameId;
        this.chatId = chatId;
        this.phase = phase;
        this.gameName = gameName;
        this.numberOfPlayers = numberOfPlayers;
        this.numberOfRounds = numberOfRounds;
        this.players = players;
        this.owner = owner;
    }

    public ChatId getChatId() {
        return chatId;
    }

    public String getGameName() {
        return gameName;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public UserId getOwner() {
        return owner;
    }

    public Phase getPhase() {
        return phase;
    }

    public GameId getGameId() {
        return gameId;
    }

    public List<Player> getPlayers() {
        if (players == null)
            return Collections.emptyList();
        else
            return players;
    }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }
}
