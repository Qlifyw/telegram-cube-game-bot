package org.cubegame.application.handler;

import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.UserId;
import org.cubegame.infrastructure.properties.ApplicationProperties;
import org.cubegame.infrastructure.repository.game.GameRepository;
import org.cubegame.infrastructure.repository.game.GameRepositoryImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventHandlerTest {

    final static ApplicationProperties applicationProperties = ApplicationProperties.load();
    final GameRepository gameRepository = new GameRepositoryImpl();
    final EventHandler eventHandler = new EventHandlerImpl(gameRepository, applicationProperties);


    private static final ChatId CHAT_ID = new ChatId(123L);
    private static final UserId USER_ID = new UserId(456L);


    @BeforeAll
    static void beforeAll() {

    }

//    @Test
//    public void main() {
//        final Message receivedMessage = new Message(CHAT_ID, USER_ID, "/start1@"+ applicationProperties.getBotName());
//
//        final TelegramBotView telegramBotView = new TelegramBotView() {
//
//            @Override
//            public void showMenu(final InlineKeyboardMarkup menu, final ChatId chatId) {
//
//            }
//
//            @Override
//            public void respond(final ResponseMessage response) {
//                Assertions.assertTrue( response.getMessage().contains("Invalid"));
//            }
//        };
//
//        eventHandler.handle(receivedMessage, applicationProperties);
//    }

}