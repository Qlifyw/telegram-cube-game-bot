package org.cubegame.infrastructure.repository.game;

import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.game.Game;

import java.util.Optional;

public interface GameRepository {

    Optional<Game> get(ChatId chatId);

    void save(Game game);

}
