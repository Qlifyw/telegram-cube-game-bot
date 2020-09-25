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
import org.cubegame.infrastructure.model.message.ResponseType;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayersAmountPhaseExecutorIT {
    private static final String BOT_NAME = "my-bot";
    private static final ApplicationProperties applicationProperties = new ApplicationProperties(BOT_NAME);
    private static final SpeechFactory speechFactory = new SpeechFactory(applicationProperties);

    private final GameRepository gameRepository = new GameRepositoryImpl();
    private final EventHandler eventHandler = new EventHandlerImpl(gameRepository, applicationProperties);

    private final PhaseExecutorFactory phaseExecutorFactory = new PhaseExecutorFactory(gameRepository, applicationProperties);

    private static final ChatId CHAT_ID = new ChatId(123L);
    private static final UserId USER_ID = new UserId(456L);
    private static final String FIRST_NAME = "First name";
    private static final Dice DICE = null;

    private static final String GAME_NAME = "cube-game";
    private static final long PLAYERS_AMOUNT = 4;

    @Test
    @DisplayName("Success when specified valid amount")
    void suceessWhenChooseGame() {
        final Message msgTemplate = new Message(CHAT_ID, USER_ID, FIRST_NAME, speechFactory.of(""), DICE);

        CascadePhaseStepper.moveUp(eventHandler, msgTemplate, commands);

        final Speech speech = speechFactory.of(String.format("@%s %d", applicationProperties.getBotName(), PLAYERS_AMOUNT));
        final Message message = new Message(CHAT_ID, USER_ID, FIRST_NAME, speech, DICE);
        final List<ResponseMessage> responses = eventHandler.handle(message);

        final PhaseExecutor phaseExecutor = phaseExecutorFactory.newInstance(
                Phase.getNextFor(Phase.NUMBER_OF_PLAYERS),
                message.getChatId()
        );

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());

        final ResponseMessage nextPhaseIntro = responses.get(0);
        assertEquals(message.getChatId(), nextPhaseIntro.getChatId());
        assertEquals(ResponseType.TEXT, nextPhaseIntro.getType());

        phaseExecutor.initiation(message.getChatId())
                .ifPresent(responseMessage ->
                        assertEquals(responseMessage.getMessage(), nextPhaseIntro.getMessage())
                );
    }

    @Test
    @DisplayName("Skip if user is not owner")
    void skipMessageIfNotTagged() {
        final UserId anotherUserId = new UserId(ThreadLocalRandom.current().nextLong());

        final Message msgTemplate = new Message(CHAT_ID, USER_ID, FIRST_NAME, speechFactory.of(GAME_NAME), DICE);

        final Speech speech = speechFactory.of(String.format("@%s %d", applicationProperties.getBotName(), PLAYERS_AMOUNT));
        final Message message = new Message(CHAT_ID, anotherUserId, FIRST_NAME, speech, DICE);

        CascadePhaseStepper.moveUp(eventHandler, msgTemplate, commands);

        final List<ResponseMessage> responses = eventHandler.handle(message);

        assertTrue(responses.isEmpty());
    }

    @ParameterizedTest(name = "Skip {0} message")
    @MethodSource("ignoredMessages")
    void skipMessageIfNotTagged(Speech speech) {
        final Message message = new Message(CHAT_ID, USER_ID, FIRST_NAME, speech, DICE);
        final List<ResponseMessage> responses = eventHandler.handle(message);

        assertTrue(responses.isEmpty());
    }

    private static Stream<Arguments> ignoredMessages() {
        return Stream.of(
                Arguments.of(speechFactory.of("Hi averyone!")),
                Arguments.of(speechFactory.of(String.valueOf(PLAYERS_AMOUNT))),
                Arguments.of(speechFactory.of(String.format("@%s%d", applicationProperties.getBotName(), PLAYERS_AMOUNT))),
                Arguments.of(speechFactory.of(String.format("@%s", applicationProperties.getBotName()))),
                Arguments.of(speechFactory.of(String.format("@%s %s %s", applicationProperties.getBotName(), "a", "b"))),
                Arguments.of(speechFactory.of(String.format("%d@%s", PLAYERS_AMOUNT, applicationProperties.getBotName())))
        );
    }

    private static final List<Reply> commands = Arrays.asList(
            new Reply(USER_ID, speechFactory.of(String.format("%s@%s", Command.START.getValue(), BOT_NAME))),
            new Reply(USER_ID, speechFactory.of(GAME_NAME))
    );
}