package org.cubegame.application.handler.phase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.cubegame.application.configuration.TestDatabaseConfiguration;
import org.cubegame.application.handler.EventHandler;
import org.cubegame.application.handler.EventHandlerImpl;
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
import org.cubegame.infrastructure.repositories.game.GameRepository;
import org.cubegame.infrastructure.repositories.game.GameRepositoryImpl;
import org.cubegame.infrastructure.repositories.round.RoundRepository;
import org.cubegame.infrastructure.repositories.round.RoundRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.containers.MongoDBContainer;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmptyPhaseExecutorIT {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final static MongoDBContainer dbContainer = TestDatabaseConfiguration.getInstance();

    private final ConnectionString connectionString = new ConnectionString("mongodb://"+dbContainer.getHost()+":"+dbContainer.getFirstMappedPort());
    private final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .retryReads(true)
            .retryWrites(true)
            .build();

    private final MongoClient mongoClient = MongoClients.create(mongoClientSettings);

    private final RoundRepository roundRepository = new RoundRepositoryImpl(mongoClient, objectMapper);
    private final GameRepository gameRepository = new GameRepositoryImpl(mongoClient, objectMapper);

    private final EventHandler eventHandler = new EventHandlerImpl(gameRepository, roundRepository, applicationProperties);

    private static final String BOT_NAME = "my-bot";
    private static final ApplicationProperties applicationProperties = new ApplicationProperties(BOT_NAME);
    private static final SpeechFactory speechFactory = new SpeechFactory(applicationProperties);

    private static final ChatId CHAT_ID = new ChatId(123L);
    private static final UserId USER_ID = new UserId(456L);
    private static final String FIRST_NAME = "First name";
    private static final Dice DICE = null;

    @AfterEach
    void cleanUp() {
        final MongoDatabase database = mongoClient.getDatabase("cube-game");
        database.drop();
    }

    @ParameterizedTest(name = "Skip {0} message")
    @MethodSource("ignoredMessages")
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

    private static Stream<Arguments> ignoredMessages() {
        return Stream.of(
                Arguments.of(speechFactory.of("Hi averyone!")),
                Arguments.of(speechFactory.of("/start")),
                Arguments.of(speechFactory.of(String.format("hello@%s", applicationProperties.getBotName()))),
                Arguments.of(speechFactory.of(String.format("/todo@%s", applicationProperties.getBotName())))
        );
    }


}