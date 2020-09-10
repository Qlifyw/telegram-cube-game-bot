package org.cubegame.application.handler;

import org.cubegame.infrastructure.repository.game.GameRepository;
import org.cubegame.infrastructure.repository.game.GameRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventHandlerImplTest {

    private final GameRepository gameRepository = new GameRepositoryImpl();

    @Test
    void qwe() {

    }

}