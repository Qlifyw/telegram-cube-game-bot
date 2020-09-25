package org.cubegame.application.handler;

import org.cubegame.application.handler.stepper.CascadePhaseStepper;
import org.cubegame.application.model.Reply;
import org.cubegame.domain.events.Command;
import org.cubegame.domain.events.Phase;
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

class PlayersAwaitingPhaseExecutorIT {
    private static final String BOT_NAME = "my-bot";
    private static final ApplicationProperties applicationProperties = new ApplicationProperties(BOT_NAME);
    private static final SpeechFactory speechFactory = new SpeechFactory(applicationProperties);

    private final GameRepository gameRepository = new GameRepositoryImpl();
    private final EventHandler eventHandler = new EventHandlerImpl(gameRepository, applicationProperties);

    private final PhaseExecutorFactory phaseExecutorFactory = new PhaseExecutorFactory(gameRepository, applicationProperties);

    private static final ChatId CHAT_ID = new ChatId(123L);
    private static final UserId USER_ID_1 = new UserId(456L);
    private static final UserId USER_ID_2 = new UserId(678L);
    private static final String FIRST_NAME = "First name";
    private static final Dice DICE = null;

    private static final String GAME_NAME = "cube-game";
    private static final long PLAYERS_AMOUNT = 2;

    @Test
    @DisplayName("Success when all players connected")
    void suceessWhenChooseGame() {
        final Message msgTemplate = new Message(CHAT_ID, USER_ID_1, FIRST_NAME, speechFactory.of(""), DICE);

        CascadePhaseStepper.moveUp(eventHandler, msgTemplate, commands);

        final Speech speech = speechFactory.of(String.format("@%s %s", applicationProperties.getBotName(), "+"));
        final Message message = new Message(CHAT_ID, USER_ID_1, FIRST_NAME, speech, DICE);
        final List<ResponseMessage> responsesAfterFirstPlayer = eventHandler.handle(message);

        assertFalse(responsesAfterFirstPlayer.isEmpty());
        assertEquals(1, responsesAfterFirstPlayer.size());
        assertTrue(responsesAfterFirstPlayer.get(0).getMessage().contains(String.format("%d players", PLAYERS_AMOUNT - 1)));


        final Speech speech2 = speechFactory.of(String.format("@%s %s", applicationProperties.getBotName(), "+"));
        final Message message2 = new Message(CHAT_ID, USER_ID_2, FIRST_NAME, speech2, DICE);
        final List<ResponseMessage> responsesAfterSecondPlayer = eventHandler.handle(message2);

        assertFalse(responsesAfterSecondPlayer.isEmpty());
        assertEquals(1, responsesAfterSecondPlayer.size());

        final PhaseExecutor phaseExecutor = phaseExecutorFactory.newInstance(
                Phase.getNextFor(Phase.NUMBER_OF_PLAYERS),
                message.getChatId()
        );

        phaseExecutor.initiation(message.getChatId())
                .ifPresent(responseMessage ->
                        assertEquals(responseMessage.getMessage(), responsesAfterSecondPlayer.get(0).getMessage())
                );
    }

    @Test
    @DisplayName("Skiped when one players connected twice")
    void skipedWhenOnePlayerConnectedTwice() {
        final Message msgTemplate = new Message(CHAT_ID, USER_ID_1, FIRST_NAME, speechFactory.of(""), DICE);
        CascadePhaseStepper.moveUp(eventHandler, msgTemplate, commands);

        final Speech speech = speechFactory.of(String.format("@%s %s", applicationProperties.getBotName(), "+"));
        final Message message = new Message(CHAT_ID, USER_ID_1, FIRST_NAME, speech, DICE);
        final List<ResponseMessage> responsesAfterFirstPlayer = eventHandler.handle(message);

        assertFalse(responsesAfterFirstPlayer.isEmpty());
        assertEquals(1, responsesAfterFirstPlayer.size());
        assertTrue(responsesAfterFirstPlayer.get(0).getMessage().contains(String.format("%d players", PLAYERS_AMOUNT - 1)));

        final List<ResponseMessage> responsesAfterSecondPlayer = eventHandler.handle(message);

        assertTrue(responsesAfterSecondPlayer.isEmpty());
    }

    @ParameterizedTest(name = "Skip {0} message")
    @MethodSource("argumentsStream")
    void skipMessageIfNotTagged(Speech speech) {
        final Message message = new Message(CHAT_ID, USER_ID_1, FIRST_NAME, speech, DICE);
        final List<ResponseMessage> responses = eventHandler.handle(message);

        assertTrue(responses.isEmpty());
    }

    static Stream<Arguments> argumentsStream() {
        return Stream.of(
                Arguments.of(speechFactory.of("Hi averyone!")),
                Arguments.of(speechFactory.of(String.valueOf(PLAYERS_AMOUNT))),
                Arguments.of(speechFactory.of(String.format("@%s%s", applicationProperties.getBotName(), "Me"))),
                Arguments.of(speechFactory.of(String.format("@%s", applicationProperties.getBotName()))),
                Arguments.of(speechFactory.of(String.format("%s@%s", "Me", applicationProperties.getBotName())))
        );
    }

    final List<Reply> commands = Arrays.asList(
            new Reply(USER_ID_1, speechFactory.of(String.format("%s@%s", Command.START.getValue(), BOT_NAME))),
            new Reply(USER_ID_1, speechFactory.of(GAME_NAME)),
            new Reply(USER_ID_1, speechFactory.of(String.format("@%s %d", BOT_NAME, PLAYERS_AMOUNT)))
    );
}