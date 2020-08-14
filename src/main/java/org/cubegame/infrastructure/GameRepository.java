package org.cubegame.infrastructure;

import org.cubegame.domain.model.ChatId;
import org.cubegame.domain.model.game.Game;

import java.util.Optional;

public interface GameRepository {

    Optional<Game> get(ChatId chatId);

    void save(Game game);

}
