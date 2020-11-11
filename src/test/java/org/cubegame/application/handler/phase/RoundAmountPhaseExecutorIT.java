package org.cubegame.application.handler.phase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import org.cubegame.application.configuration.TestDatabaseConfiguration;
import org.cubegame.application.executors.factory.PhaseExecutor;
import org.cubegame.application.executors.factory.PhaseExecutorFactory;
import org.cubegame.application.handler.EventHandler;
import org.cubegame.application.handler.EventHandlerImpl;
import org.cubegame.application.handler.stepper.CascadePhaseStepper;
import org.cubegame.application.model.Reply;
import org.cubegame.domain.events.Command;
import org.cubegame.domain.model.dice.Dice;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.UserId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.domain.model.message.speach.Speech;
import org.cubegame.domain.model.message.speach.SpeechFactory;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.ResponseType;
import org.cubegame.infrastructure.properties.ApplicationProperties;
import org.cubegame.infrastructure.repositories.game.GameRepository;
import org.cubegame.infrastructure.repositories.game.GameRepositoryImpl;
import org.cubegame.infrastructure.repositories.round.RoundRepository;
import org.cubegame.infrastructure.repositories.round.RoundRepositoryImpl;
import org.cubegame.infrastructure.services.CommandValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.containers.MongoDBContainer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoundAmountPhaseExecutorIT {
    private static final String BOT_NAME = "my-bot";
    private static final ApplicationProperties applicationProperties = new ApplicationProperties(BOT_NAME);
    private static final SpeechFactory speechFactory = new SpeechFactory(applicationProperties);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MongoDBContainer dbContainer = TestDatabaseConfiguration.getInstance();
    private final MongoClient mongoClient = new MongoClient(dbContainer.getHost(), dbContainer.getFirstMappedPort());
    private final RoundRepository roundRepository = new RoundRepositoryImpl(mongoClient, objectMapper);
    private final GameRepository gameRepository = new GameRepositoryImpl(mongoClient, objectMapper);

    private final CommandValidator commandValidator = new CommandValidator(applicationProperties);
    private final EventHandler eventHandler = new EventHandlerImpl(gameRepository, roundRepository, applicationProperties);

    private final PhaseExecutorFactory phaseExecutorFactory = new PhaseExecutorFactory(gameRepository, roundRepository, commandValidator);

    private static final ChatId CHAT_ID = new ChatId(123L);
    private static final UserId USER_ID = new UserId(456L);
    private static final String FIRST_NAME = "First name";
    private static final Dice DICE = null;

    private static final String GAME_NAME = "cube-game";
    private static final long ROUNDS_AMOUNT = 2;

    @AfterEach
    void cleanUp() {
        mongoClient.dropDatabase("cube-game");
    }

    @Test
    @DisplayName("Success when specified valid amount")
    void suceessWhenChooseGame() {
        final Message msgTemplate = new Message(CHAT_ID, USER_ID, FIRST_NAME, SpeechFactory.EMPTY_SPEECH, DICE);

        CascadePhaseStepper.moveUp(eventHandler, msgTemplate, commands);

        final Speech speech = speechFactory.of(String.format("@%s %d", applicationProperties.getBotName(), ROUNDS_AMOUNT));
        final Message message = new Message(CHAT_ID, USER_ID, FIRST_NAME, speech, DICE);
        final List<ResponseMessage> responses = eventHandler.handle(message);

        final Game storedGame = gameRepository.getActive(CHAT_ID).get();
        final PhaseExecutor phaseExecutor = phaseExecutorFactory
                .newInstance(storedGame.getPhase(), message.getChatId());

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());

        final ResponseMessage nextPhaseIntro = responses.get(0);
        assertEquals(message.getChatId(), nextPhaseIntro.getChatId());
        assertEquals(ResponseType.TEXT, nextPhaseIntro.getType());

        phaseExecutor.initiation()
                .ifPresent(responseMessage ->
                        assertEquals(nextPhaseIntro.getMessage(), responseMessage.getMessage())
                );
    }

    @ParameterizedTest
    @MethodSource("invalidNumbers")
    @DisplayName("Failed when specified invalid amount")
    void failedWhenInvalidNumber(Speech speech) {
        final Message msgTemplate = new Message(CHAT_ID, USER_ID, FIRST_NAME, SpeechFactory.EMPTY_SPEECH, DICE);

        CascadePhaseStepper.moveUp(eventHandler, msgTemplate, commands);

        final Message message = new Message(CHAT_ID, USER_ID, FIRST_NAME, speech, DICE);
        final List<ResponseMessage> responses = eventHandler.handle(message);

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());

        final ResponseMessage responseMessage = responses.get(0);
        assertEquals(message.getChatId(), responseMessage.getChatId());
        assertTrue(responseMessage.getMessage().contains("Invalid"));
    }

    @Test
    @DisplayName("Skip if user is not owner")
    void skipMessageIfNotTagged() {
        final UserId anotherUserId = new UserId(ThreadLocalRandom.current().nextLong());

        final Message msgTemplate = new Message(CHAT_ID, USER_ID, FIRST_NAME, speechFactory.of(GAME_NAME), DICE);

        final Speech speech = speechFactory.of(String.format("@%s %d", applicationProperties.getBotName(), ROUNDS_AMOUNT));
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
                Arguments.of(speechFactory.of(String.valueOf(ROUNDS_AMOUNT))),
                Arguments.of(speechFactory.of(String.format("@%s%d", applicationProperties.getBotName(), ROUNDS_AMOUNT))),
                Arguments.of(speechFactory.of(String.format("@%s", applicationProperties.getBotName()))),
                Arguments.of(speechFactory.of(String.format("@%s %s %s", applicationProperties.getBotName(), "a", "b"))),
                Arguments.of(speechFactory.of(String.format("%d@%s", ROUNDS_AMOUNT, applicationProperties.getBotName())))
        );
    }


    private static Stream<Arguments> invalidNumbers() {
        return Stream.of(
                Arguments.of(speechFactory.of(String.format("@%s %d", applicationProperties.getBotName(), 0))),
                Arguments.of(speechFactory.of(String.format("@%s %f", applicationProperties.getBotName(), 1.2))),
                Arguments.of(speechFactory.of(String.format("@%s %d", applicationProperties.getBotName(), -1)))
        );
    }

    private static final List<Reply> commands = Arrays.asList(
            new Reply(USER_ID, speechFactory.of(String.format("%s@%s", Command.START.getValue(), BOT_NAME))),
            new Reply(USER_ID, speechFactory.of(GAME_NAME))
    );
}