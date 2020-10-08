package org.cubegame.domain.model.game;

import org.cubegame.domain.events.Phase;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.GameId;
import org.cubegame.domain.model.identifier.UserId;

import java.util.List;
import java.util.UUID;

public class GameBuilder {
    private GameId gameId;
    private ChatId chatId;
    private Phase phase;
    private String gameName;
    private int numberOfPlayers;
    private int numberOfRounds;
    private List<Player> players;
    private UserId owner;


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

    public GameBuilder setNumberOfPlayers(final int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
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

    public GameBuilder setOwner(final UserId owner) {
        this.owner = owner;
        return this;
    }

    public GameBuilder setNumberOfRounds(final int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
        return this;
    }

    public static GameBuilder from(Game game) {
        return new GameBuilder()
                .setGameId(game.getGameId())
                .setChatId(game.getChatId())
                .setGameName(game.getGameName())
                .setPhase(game.getPhase())
                .setNumberOfPlayers(game.getNumberOfPlayers())
                .setNumberOfRounds(game.getNumberOfRounds())
                .setPlayers(game.getPlayers())
                .setOwner(game.getOwner());
    }

    public Game build() {
        final GameId gameIdentifier = gameId == null ? new GameId(UUID.randomUUID()) : gameId;
        return new Game(gameIdentifier, chatId, phase, gameName, numberOfPlayers, numberOfRounds, players, owner);
    }
}