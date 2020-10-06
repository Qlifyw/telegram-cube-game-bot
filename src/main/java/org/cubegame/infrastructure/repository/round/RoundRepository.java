package org.cubegame.infrastructure.repository.round;

import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.round.Round;

import java.util.Optional;

public interface RoundRepository {

    Optional<Round> get(ChatId chatId);

    void save(Round round);

}
