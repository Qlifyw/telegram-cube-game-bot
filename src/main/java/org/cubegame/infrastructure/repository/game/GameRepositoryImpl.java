package org.cubegame.infrastructure.repository.game;

import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.identifier.ChatId;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class GameRepositoryImpl implements GameRepository {

    private Map<ChatId, Game> games = new LinkedHashMap();

    @Override
    public Optional<Game> get(ChatId chatId) {
//        final Game game = new GameBuilder()
//                .setChatId(new ChatId(123456789L))
//                .setPhase(Phase.CHOOSE_GAME)
//                .setGameName("cube-game")
//                .setNumerOfPlayers(3)
//                .build();

//        return Optional.of(game);

        final Game storedGame = games.get(chatId);
        if (storedGame == null) {
            return Optional.empty();
        } else {
            return Optional.of(storedGame);
        }
    }

    @Override
    public void save(final Game game) {
        this.games.put(game.getChatId(), game);
    }
}
