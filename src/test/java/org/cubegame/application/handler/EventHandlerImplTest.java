package org.cubegame.application.handler;

import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.UserId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.domain.model.message.speach.Comment;
import org.cubegame.domain.model.message.speach.Speech;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.properties.ApplicationProperties;
import org.cubegame.infrastructure.repository.game.GameRepository;
import org.cubegame.infrastructure.repository.game.GameRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.telegram.telegrambots.meta.api.objects.Dice;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventHandlerImplTest {

    private final ApplicationProperties applicationProperties = ApplicationProperties.load();
    private final GameRepository gameRepository = new GameRepositoryImpl();
    private final EventHandler eventHandler = new EventHandlerImpl(gameRepository, applicationProperties);

    private static final ChatId CHAT_ID = new ChatId(123L);
    private static final UserId USER_ID = new UserId(456L);
    private static final String FIRST_NAME = "First name";
    private static final Dice DICE = null;

    @Test
    void qwe() {
        final Speech comment = new Comment("Hi averyone!");
        final Message message = new Message(CHAT_ID, USER_ID, FIRST_NAME, comment, DICE);
        final List<ResponseMessage> responses = eventHandler.handle(message);
    }

}