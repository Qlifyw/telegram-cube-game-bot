package org.cubegame.infrastructure.repository.game;

import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.game.Game;

import java.util.Optional;

public class GameRepositoryImpl implements GameRepository {

    private Game game = null;

    @Override
    public Optional<Game> get(ChatId chatId) {
//        final Game game = new GameBuilder()
//                .setChatId(new ChatId(123456789L))
//                .setPhase(Phase.CHOOSE_GAME)
//                .setGameName("cube-game")
//                .setNumerOfPlayers(3)
//                .build();

//        return Optional.of(game);

        if (game == null) {
            return Optional.empty();
        } else {
            return Optional.of(game);
        }
    }

    @Override
    public void save(final Game game) {
        this.game = game;
    }
}
