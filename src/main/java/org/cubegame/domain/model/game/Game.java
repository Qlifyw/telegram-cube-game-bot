package org.cubegame.domain.model.game;

import org.cubegame.domain.model.ChatId;
import org.cubegame.domain.events.Phase;

public class Game {

    private final ChatId chatId;
    private final String gameName;
    private final int numerOfPlayers;
    private final Phase phase;

    public Game(ChatId chatId, Phase phase, String gameName, int numerOfPlayers) {
        this.chatId = chatId;
        this.phase = phase;
        this.gameName = gameName;
        this.numerOfPlayers = numerOfPlayers;
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

    public Phase getPhase() {
        return phase;
    }


}
