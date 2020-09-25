package org.cubegame.application.handler;

import org.cubegame.domain.events.Command;
import org.cubegame.domain.model.dice.Dice;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.UserId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.domain.model.message.speach.Speech;
import org.cubegame.domain.model.message.speach.SpeechFactory;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.ResponseType;
import org.cubegame.infrastructure.properties.ApplicationProperties;
import org.cubegame.infrastructure.repository.game.GameRepository;
import org.cubegame.infrastructure.repository.game.GameRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmptyPhaseExecutorIT {

    private final GameRepository gameRepository = new GameRepositoryImpl();
    private final EventHandler eventHandler = new EventHandlerImpl(gameRepository, applicationProperties);

    private static final String BOT_NAME = "my-bot";
    private static final ApplicationProperties applicationProperties = new ApplicationProperties(BOT_NAME);
    private static final SpeechFactory speechFactory = new SpeechFactory(applicationProperties);

    private static final ChatId CHAT_ID = new ChatId(123L);
    private static final UserId USER_ID = new UserId(456L);
    private static final String FIRST_NAME = "First name";
    private static final Dice DICE = null;


    static Stream<Arguments> argumentsStream() {
        return Stream.of(
                Arguments.of(speechFactory.of("Hi averyone!")),
                Arguments.of(speechFactory.of("/start")),
                Arguments.of(speechFactory.of(String.format("hello@%s", applicationProperties.getBotName()))),
                Arguments.of(speechFactory.of(String.format("/todo@%s", applicationProperties.getBotName()))),
                Arguments.of(speechFactory.of(String.format("/todo@%s", applicationProperties.getBotName())))
        );
    }

    @ParameterizedTest(name = "Skip {0} message")
    @MethodSource("argumentsStream")
    void skipMessageIfNotTagged(Speech speech) {
        final Message message = new Message(CHAT_ID, USER_ID, FIRST_NAME, speech, DICE);
        final List<ResponseMessage> responses = eventHandler.handle(message);

        assertTrue(responses.isEmpty());
    }

    @Test
    @DisplayName("Processed valid command message if bot tagged")
    void processedInvalidCommandMessageIfTagged() {
        final Speech comment = speechFactory.of(String.format("%s@%s", Command.START.getValue(), applicationProperties.getBotName()));
        final Message message = new Message(CHAT_ID, USER_ID, FIRST_NAME, comment, DICE);
        final List<ResponseMessage> responses = eventHandler.handle(message);

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals(message.getChatId(), responses.get(0).getChatId());
        assertEquals(ResponseType.NAVIAGTION, responses.get(0).getType());
    }


}