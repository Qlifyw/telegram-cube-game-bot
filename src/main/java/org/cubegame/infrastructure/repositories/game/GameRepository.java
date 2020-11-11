package org.cubegame.infrastructure.repositories.game;

import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.identifier.ChatId;

import java.util.Optional;

public interface GameRepository {

    Optional<Game> getActive(ChatId chatId);

    void save(Game game);

    void update(Game game);
}
