package org.cubegame.infrastructure.repository.round;

import org.cubegame.domain.model.identifier.GameId;
import org.cubegame.domain.model.round.Round;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class RoundRepositoryImpl implements RoundRepository {

    private Map<GameId, Round> rounds = new LinkedHashMap();

    @Override
    public Optional<Round> get(final GameId gameId) {
        final Round stored = rounds.get(gameId);
        return Optional.empty();
    }

    @Override
    public void save(final Round round) {
        rounds.put(round.getRelatedGame(), round);
    }
}
