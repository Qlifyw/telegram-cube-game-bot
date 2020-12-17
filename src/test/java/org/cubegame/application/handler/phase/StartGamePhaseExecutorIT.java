package org.cubegame.application.handler.phase;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.cubegame.application.configuration.TestDatabaseConfiguration;
import org.cubegame.application.handler.EventHandler;
import org.cubegame.application.handler.EventHandlerImpl;
import org.cubegame.application.handler.stepper.CascadePhaseStepper;
import org.cubegame.application.model.Reply;
import org.cubegame.application.repositories.game.GameRepository;
import org.cubegame.application.repositories.round.RoundRepository;
import org.cubegame.domain.events.Command;
import org.cubegame.domain.model.dice.Dice;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.MessageId;
import org.cubegame.domain.model.identifier.UserId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.domain.model.message.speach.Speech;
import org.cubegame.domain.model.message.speach.SpeechFactory;
import org.cubegame.infrastructure.model.message.type.ResponseMessage;
import org.cubegame.infrastructure.properties.ApplicationProperties;
import org.cubegame.infrastructure.repositories.game.GameRepositoryImpl;
import org.cubegame.infrastructure.repositories.round.RoundRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.containers.MongoDBContainer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StartGamePhaseExecutorIT {
    private static final ApplicationProperties applicationProperties = ApplicationProperties.load();
    private static final String BOT_NAME = applicationProperties.getBotName();
    private static final SpeechFactory speechFactory = new SpeechFactory(applicationProperties);

    private final static MongoDBContainer dbContainer = TestDatabaseConfiguration.getInstance();

    private static final ConnectionString connectionString = new ConnectionString("mongodb://"+dbContainer.getHost()+":"+dbContainer.getFirstMappedPort());
    private static final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .retryReads(true)
            .retryWrites(true)
            .build();

    private static final MongoClient mongoClient = MongoClients.create(mongoClientSettings);

    private static final RoundRepository roundRepository = new RoundRepositoryImpl(mongoClient);
    private static final GameRepository gameRepository = new GameRepositoryImpl(mongoClient);

    private final EventHandler eventHandler = new EventHandlerImpl(gameRepository, roundRepository, applicationProperties);

    private static final ChatId CHAT_ID = new ChatId(123L);
    private static final UserId USER_ID_1 = new UserId(456L);
    private static final UserId USER_ID_2 = new UserId(678L);

    private static final Speech SPEECH = SpeechFactory.EMPTY_SPEECH;

    private static final MessageId FORWARDED = null;

    private static final String GAME_NAME = "cube-game";
    private static final long PLAYERS_AMOUNT = 2;
    private static final long ROUNDS_AMOUNT = 2;

    @Test
    @DisplayName("Success when all players moved")
    void successWhenAllPlayersMoved() {
        final Message msgTemplate = new Message(CHAT_ID, USER_ID_1, generateUserName(USER_ID_1), SpeechFactory.EMPTY_SPEECH, null, FORWARDED);

        CascadePhaseStepper.moveUp(eventHandler, msgTemplate, commands);

        // first round
        final Message message = new Message(CHAT_ID, USER_ID_1, generateUserName(USER_ID_1), SpeechFactory.EMPTY_SPEECH, new Dice(4), FORWARDED);
        final List<ResponseMessage> responsesAfterFirstPlayer = eventHandler.handle(message);
        assertTrue(responsesAfterFirstPlayer.isEmpty());

        final Message message2 = new Message(CHAT_ID, USER_ID_2, generateUserName(USER_ID_2), SpeechFactory.EMPTY_SPEECH, new Dice(2), FORWARDED);
        final List<ResponseMessage> responsesAfterSecondPlayer = eventHandler.handle(message2);
        assertFalse(responsesAfterSecondPlayer.isEmpty());
        assertEquals(1, responsesAfterSecondPlayer.size());

        final String[] splited = responsesAfterSecondPlayer.get(0).getMessage().split("\n");

        final int PLAYERS = 2;
        final int TITLE = 1;
        assertEquals(PLAYERS + TITLE, splited.length);
    }

    @Test
    @DisplayName("Skiped when one players moved twice")
    void skipedWhenOnePlayerMovedTwice() {
        final Message msgTemplate = new Message(CHAT_ID, USER_ID_1, generateUserName(USER_ID_1), SpeechFactory.EMPTY_SPEECH, null, FORWARDED);
        CascadePhaseStepper.moveUp(eventHandler, msgTemplate, commands);

        final Dice dice = new Dice(2);
        final Message message = new Message(CHAT_ID, USER_ID_1, generateUserName(USER_ID_1), SPEECH, dice, FORWARDED);
        final List<ResponseMessage> responsesAfterFirstThrow = eventHandler.handle(message);

        assertTrue(responsesAfterFirstThrow.isEmpty());

        final List<ResponseMessage> responsesAfterSecondThrow = eventHandler.handle(message);

        assertTrue(responsesAfterSecondThrow.isEmpty());
    }

    @Test
    @DisplayName("Skiped when player forward message")
    void SkipedWhenPlayerForwardMessage() {
        final Message msgTemplate = new Message(CHAT_ID, USER_ID_1, generateUserName(USER_ID_1), SpeechFactory.EMPTY_SPEECH, null, FORWARDED);
        CascadePhaseStepper.moveUp(eventHandler, msgTemplate, commands);

        // first round
        final Message message_round1_player1 = new Message(CHAT_ID, USER_ID_1, generateUserName(USER_ID_1), SPEECH, new Dice(6), FORWARDED);
        final List<ResponseMessage> responsesR1P1 = eventHandler.handle(message_round1_player1);
        assertTrue(responsesR1P1.isEmpty());

        final Message message_round1_player2 = new Message(CHAT_ID, USER_ID_2, generateUserName(USER_ID_1), SPEECH, new Dice(2), FORWARDED);
        final List<ResponseMessage> responsesR1P2 = eventHandler.handle(message_round1_player2);
        assertFalse(responsesR1P2.isEmpty());

        // second round
        final Message message_round2_player1 = new Message(CHAT_ID, USER_ID_1, generateUserName(USER_ID_1), SPEECH, new Dice(6), new MessageId(1));
        final List<ResponseMessage> responsesR2P1 = eventHandler.handle(message_round2_player1);
        assertTrue(responsesR2P1.isEmpty());

        final Message message_round2_player2 = new Message(CHAT_ID, USER_ID_2, generateUserName(USER_ID_1), SPEECH, new Dice(2), FORWARDED);
        final List<ResponseMessage> responsesR2P2 = eventHandler.handle(message_round2_player2);
        assertTrue(responsesR2P2.isEmpty());

        final Message message_round2_player1_next = new Message(CHAT_ID, USER_ID_1, generateUserName(USER_ID_1), SPEECH, new Dice(4), FORWARDED);
        final List<ResponseMessage> responsesR2P1_next = eventHandler.handle(message_round2_player1_next);
        assertFalse(responsesR2P1_next.isEmpty());

    }

    @Test
    @DisplayName("Can cancel game")
    void canCancellGame() {
        final Message msgTemplate = new Message(CHAT_ID, USER_ID_1, generateUserName(USER_ID_1), SpeechFactory.EMPTY_SPEECH, null, FORWARDED);
        CascadePhaseStepper.moveUp(eventHandler, msgTemplate, commands);

        // first throw
        final Message message_round1_player1 = new Message(CHAT_ID, USER_ID_1, generateUserName(USER_ID_1), SPEECH, null, FORWARDED);
        final List<ResponseMessage> responsesR1P1 = eventHandler.handle(message_round1_player1);
        assertTrue(responsesR1P1.isEmpty());

        // cancel game
        final Speech cancelCommand = speechFactory.of(String.format("%s@%s", Command.STOP.getValue(), BOT_NAME));
        final Message message_round1_player2 = new Message(CHAT_ID, USER_ID_2, generateUserName(USER_ID_1), cancelCommand, null, FORWARDED);
        final List<ResponseMessage> responsesR1P2 = eventHandler.handle(message_round1_player2);
        assertTrue(responsesR1P2.isEmpty());

        Optional<Game> activeGames = gameRepository.getActive(CHAT_ID);
        assertFalse(activeGames.isPresent());
    }

    @Test
    @DisplayName("Game successfully completed")
    void gameSuccessfullyCompleted() {
        final Message msgTemplate = new Message(CHAT_ID, USER_ID_1, generateUserName(USER_ID_1), SpeechFactory.EMPTY_SPEECH, null, FORWARDED);
        CascadePhaseStepper.moveUp(eventHandler, msgTemplate, commands);

        // first round
        final Message message_round1_player1 = new Message(CHAT_ID, USER_ID_1, generateUserName(USER_ID_1), SPEECH, new Dice(6), FORWARDED);
        final List<ResponseMessage> responsesR1P1 = eventHandler.handle(message_round1_player1);
        assertTrue(responsesR1P1.isEmpty());

        final Message message_round1_player2 = new Message(CHAT_ID, USER_ID_2, generateUserName(USER_ID_1), SPEECH, new Dice(2), FORWARDED);
        final List<ResponseMessage> responsesR1P2 = eventHandler.handle(message_round1_player2);
        assertFalse(responsesR1P2.isEmpty());

        // second round
        final Message message_round2_player1 = new Message(CHAT_ID, USER_ID_1, generateUserName(USER_ID_1), SPEECH, new Dice(6), FORWARDED);
        final List<ResponseMessage> responsesR2P1 = eventHandler.handle(message_round2_player1);
        assertTrue(responsesR2P1.isEmpty());

        final Message message_round2_player2 = new Message(CHAT_ID, USER_ID_2, generateUserName(USER_ID_1), SPEECH, new Dice(3), FORWARDED);
        final List<ResponseMessage> responsesR2P2 = eventHandler.handle(message_round2_player2);
        assertFalse(responsesR2P2.isEmpty());

        // check game completed
        Optional<Game> activeGames = gameRepository.getActive(CHAT_ID);
        assertFalse(activeGames.isPresent());
    }

    @ParameterizedTest(name = "Skip {0} message")
    @MethodSource("ignoredMessages")
    void skipMessageIfNotTagged(Speech speech) {
        final Message message = new Message(CHAT_ID, USER_ID_1, generateUserName(USER_ID_1), speech, null, FORWARDED);
        final List<ResponseMessage> responses = eventHandler.handle(message);

        assertTrue(responses.isEmpty());
    }

    private static Stream<Arguments> ignoredMessages() {
        return Stream.of(
                Arguments.of(speechFactory.of("Hi averyone!")),
                Arguments.of(speechFactory.of(String.valueOf(PLAYERS_AMOUNT))),
                Arguments.of(speechFactory.of(String.format("@%s%s", applicationProperties.getBotName(), "Me"))),
                Arguments.of(speechFactory.of(String.format("@%s", applicationProperties.getBotName()))),
                Arguments.of(speechFactory.of(String.format("@%s %s %s", applicationProperties.getBotName(), "a", "b"))),
                Arguments.of(speechFactory.of(String.format("%s@%s", "Me", applicationProperties.getBotName()))),
                Arguments.of(speechFactory.of(String.format("@%s %s", applicationProperties.getBotName(), "Me")))
        );
    }

    private static final List<Reply> commands = Arrays.asList(
            new Reply(USER_ID_1, speechFactory.of(String.format("%s@%s", Command.START.getValue(), BOT_NAME))),
            new Reply(USER_ID_1, speechFactory.of(GAME_NAME)),
            new Reply(USER_ID_1, speechFactory.of(String.format("@%s %d", BOT_NAME, ROUNDS_AMOUNT))),
            new Reply(USER_ID_1, speechFactory.of(String.format("@%s %d", BOT_NAME, PLAYERS_AMOUNT))),
            new Reply(USER_ID_1, speechFactory.of(String.format("@%s %s", BOT_NAME, "+"))),
            new Reply(USER_ID_2, speechFactory.of(String.format("@%s %s", BOT_NAME, "++")))
    );

    private static String generateUserName(UserId userId) {
        return String.format("Player-%s", userId.getValue());
    }
}