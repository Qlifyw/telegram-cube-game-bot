package org.cubegame.application.handler;

import org.cubegame.application.handler.stepper.CascadePhaseStepper;
import org.cubegame.application.model.Reply;
import org.cubegame.domain.events.Command;
import org.cubegame.domain.model.dice.Dice;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.UserId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.domain.model.message.speach.Speech;
import org.cubegame.domain.model.message.speach.SpeechFactory;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.properties.ApplicationProperties;
import org.cubegame.infrastructure.repository.game.GameRepository;
import org.cubegame.infrastructure.repository.game.GameRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StartGamePhaseExecutorIT {
    private static final ApplicationProperties applicationProperties = ApplicationProperties.load();
    private static final String BOT_NAME = applicationProperties.getBotName();

    private final GameRepository gameRepository = new GameRepositoryImpl();
    private final EventHandler eventHandler = new EventHandlerImpl(gameRepository, applicationProperties);

    private final PhaseExecutorFactory phaseExecutorFactory = new PhaseExecutorFactory(gameRepository);

    private static final ChatId CHAT_ID = new ChatId(123L);
    private static final UserId USER_ID_1 = new UserId(456L);
    private static final UserId USER_ID_2 = new UserId(678L);

    private static final Speech SPEECH = SpeechFactory.of("");

    private static final String GAME_NAME = "cube-game";
    private static final long PLAYERS_AMOUNT = 2;

    @Test
    @DisplayName("Success when all players moved")
    void suceessWhenAllPlayersMoved() {
        final Message msgTemplate = new Message(CHAT_ID, USER_ID_1, generateUserName(USER_ID_1), SpeechFactory.of(""), null);

        CascadePhaseStepper.moveUp(eventHandler, msgTemplate, commands);

        final Dice firstDice = new Dice(4);
        final Message message = new Message(CHAT_ID, USER_ID_1, generateUserName(USER_ID_1), SpeechFactory.of(""), firstDice);
        final List<ResponseMessage> responsesAfterFirstPlayer = eventHandler.handle(message);

        assertTrue(responsesAfterFirstPlayer.isEmpty());

        final Dice secondDice = new Dice(2);
        final Message message2 = new Message(CHAT_ID, USER_ID_2, generateUserName(USER_ID_2), SpeechFactory.of(""), secondDice);
        final List<ResponseMessage> responsesAfterSecondPlayer = eventHandler.handle(message2);

        assertFalse(responsesAfterSecondPlayer.isEmpty());
        assertEquals(1, responsesAfterSecondPlayer.size());

        final String[] splited = responsesAfterSecondPlayer.get(0).getMessage().split("\n");

        final int PLAYERS = 2;
        final int TITLE = 1;
        assertEquals(PLAYERS+TITLE, splited.length);
    }

    @Test
    @DisplayName("Skiped when one players moved twice")
    void skipedWhenOnePlayerMovedTwice() {
        final Message msgTemplate = new Message(CHAT_ID, USER_ID_1, generateUserName(USER_ID_1), SpeechFactory.of(""), null);
        CascadePhaseStepper.moveUp(eventHandler, msgTemplate, commands);

        final Dice dice = new Dice(2);
        final Message message = new Message(CHAT_ID, USER_ID_1, generateUserName(USER_ID_1), SPEECH, dice);
        final List<ResponseMessage> responsesAfterFirstThrow = eventHandler.handle(message);

        assertTrue(responsesAfterFirstThrow.isEmpty());

        final List<ResponseMessage> responsesAfterSecondThrow = eventHandler.handle(message);

        assertTrue(responsesAfterSecondThrow.isEmpty());
    }

    @ParameterizedTest(name = "Skip {0} message")
    @MethodSource("argumentsStream")
    void skipMessageIfNotTagged(Speech speech) {
        final Message message = new Message(CHAT_ID, USER_ID_1, generateUserName(USER_ID_1), speech, null);
        final List<ResponseMessage> responses = eventHandler.handle(message);

        assertTrue(responses.isEmpty());
    }

    static Stream<Arguments> argumentsStream() {
        return Stream.of(
                Arguments.of(SpeechFactory.of("Hi averyone!")),
                Arguments.of(SpeechFactory.of(String.valueOf(PLAYERS_AMOUNT))),
                Arguments.of(SpeechFactory.of(String.format("@%s%s", applicationProperties.getBotName(), "Me"))),
                Arguments.of(SpeechFactory.of(String.format("@%s", applicationProperties.getBotName()))),
                Arguments.of(SpeechFactory.of(String.format("%s@%s", "Me", applicationProperties.getBotName()))),
                Arguments.of(SpeechFactory.of(String.format("@%s %s", applicationProperties.getBotName(), "Me")))
        );
    }

    final List<Reply> commands = Arrays.asList(
            new Reply(USER_ID_1, SpeechFactory.of(String.format("%s@%s", Command.START.getValue(), BOT_NAME))),
            new Reply(USER_ID_1, SpeechFactory.of(GAME_NAME)),
            new Reply(USER_ID_1, SpeechFactory.of(String.format("@%s %d", BOT_NAME, PLAYERS_AMOUNT))),
            new Reply(USER_ID_1, SpeechFactory.of(String.format("@%s %s", BOT_NAME, "+"))),
            new Reply(USER_ID_2, SpeechFactory.of(String.format("@%s %s", BOT_NAME, "++")))
    );

    private static String generateUserName(UserId userId) {
        return String.format("Player-%s", userId.getValue());
    }
}