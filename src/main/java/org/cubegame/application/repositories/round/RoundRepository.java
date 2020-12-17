package org.cubegame.application.repositories.round;

import org.cubegame.domain.model.identifier.GameId;
import org.cubegame.domain.model.round.Round;

import java.util.Optional;

public interface RoundRepository {

    Optional<Round> get(GameId gameId);

    void save(Round round);

}
