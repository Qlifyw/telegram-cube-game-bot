package org.cubegame.domain.model.game;

import org.cubegame.domain.events.Phase;
import org.cubegame.domain.model.ChatId;
import org.cubegame.domain.model.GameId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {

    private final GameId gameId;
    private final ChatId chatId;
    private final String gameName;
    private final int numerOfPlayers;
    private final Phase phase;
    private final List<Player> players;


    public Game(GameId gameId, ChatId chatId, Phase phase, String gameName, int numerOfPlayers, final List<Player> players) {
        this.gameId = gameId;
        this.chatId = chatId;
        this.phase = phase;
        this.gameName = gameName;
        this.numerOfPlayers = numerOfPlayers;
        this.players = players;
    }

    public ChatId getChatId() {
        return chatId;
    }

    public String getGameName() {
        return gameName;
    }

    public int getNumerOfPlayers() {
        return numerOfPlayers;
    }

    public Phase getPhase() { return phase; }

    public GameId getGameId() { return gameId; }

    public List<Player> getPlayers() {
        if (players == null)
            return Collections.emptyList();
        else
            return players;
    }
}
