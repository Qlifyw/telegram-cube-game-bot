package org.cubegame.infrastructure.repository.round;

import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.round.Round;

import java.util.Optional;

public class RoundRepositoryImpl implements RoundRepository {

    @Override
    public Optional<Round> get(final ChatId chatId) {
        return Optional.empty();
    }

    @Override
    public void save(final Round round) {

    }
}
