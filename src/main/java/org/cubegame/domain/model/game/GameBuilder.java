package org.cubegame.domain.model.game;

import org.cubegame.domain.events.Phase;
import org.cubegame.domain.model.ChatId;
import org.cubegame.domain.model.GameId;

import java.util.List;

public class GameBuilder {
    private GameId gameId;
    private ChatId chatId;
    private Phase phase;
    private String gameName;
    private int numerOfPlayers;
    private List<Player> players;


    public GameBuilder setChatId(final ChatId chatId) {
        this.chatId = chatId;
        return this;
    }

    public GameBuilder setPhase(final Phase phase) {
        this.phase = phase;
        return this;
    }

    public GameBuilder setGameName(final String gameName) {
        this.gameName = gameName;
        return this;
    }

    public GameBuilder setNumerOfPlayers(final int numerOfPlayers) {
        this.numerOfPlayers = numerOfPlayers;
        return this;
    }

    public GameBuilder setPlayers(final List<Player> players) {
        this.players = players;
        return this;
    }

    public GameBuilder setGameId(final GameId gameId) {
        this.gameId = gameId;
        return this;
    }

    public static GameBuilder from(Game game) {
        return new GameBuilder()
                .setGameId(game.getGameId())
                .setChatId(game.getChatId())
                .setGameName(game.getGameName())
                .setPhase(game.getPhase())
                .setNumerOfPlayers(game.getNumerOfPlayers())
                .setPlayers(game.getPlayers());
    }

    public Game build() {
        return new Game(gameId, chatId, phase, gameName, numerOfPlayers, players);
    }

}