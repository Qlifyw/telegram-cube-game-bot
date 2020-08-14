package org.cubegame.domain.model.game;

import org.cubegame.domain.events.Phase;
import org.cubegame.domain.model.ChatId;

public class GameBuilder {
    private ChatId chatId;
    private Phase phase;
    private String gameName;
    private int numerOfPlayers;

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

    public static GameBuilder from(Game game) {
        return new GameBuilder()
                .setChatId(game.getChatId())
                .setGameName(game.getGameName())
                .setPhase(game.getPhase())
                .setNumerOfPlayers(game.getNumerOfPlayers());
    }

    public Game build() {
        return new Game(chatId, phase, gameName, numerOfPlayers);
    }
}