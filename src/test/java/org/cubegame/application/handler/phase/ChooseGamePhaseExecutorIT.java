package org.cubegame.application.handler.phase;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.cubegame.application.configuration.TestDatabaseConfiguration;
import org.cubegame.application.executors.factory.PhaseExecutor;
import org.cubegame.application.executors.factory.PhaseExecutorFactory;
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
import org.cubegame.infrastructure.model.message.type.ResponseType;
import org.cubegame.infrastructure.properties.ApplicationProperties;
import org.cubegame.infrastructure.repositories.game.GameRepositoryImpl;
import org.cubegame.infrastructure.repositories.round.RoundRepositoryImpl;
import org.cubegame.infrastructure.services.CommandValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

class ChooseGamePhaseExecutorIT {
    private static final String BOT_NAME = "my-bot";
    private static final ApplicationProperties applicationProperties = new ApplicationProperties(BOT_NAME);
    private static final SpeechFactory speechFactory = new SpeechFactory(applicationProperties);

    private final MongoDBContainer dbContainer = TestDatabaseConfiguration.getInstance();

    private final ConnectionString connectionString = new ConnectionString("mongodb://"+dbContainer.getHost()+":"+dbContainer.getFirstMappedPort());
    private final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .retryReads(true)
            .retryWrites(true)
            .build();

    private final MongoClient mongoClient = MongoClients.create(mongoClientSettings);

    private final RoundRepository roundRepository = new RoundRepositoryImpl(mongoClient);
    private final GameRepository gameRepository = new GameRepositoryImpl(mongoClient);

    private final CommandValidator commandValidator = new CommandValidator(applicationProperties);
    private final EventHandler eventHandler = new EventHandlerImpl(gameRepository, roundRepository, applicationProperties);

    private final PhaseExecutorFactory phaseExecutorFactory = new PhaseExecutorFactory(gameRepository, roundRepository, commandValidator);

    private static final ChatId CHAT_ID = new ChatId(123L);
    private static final UserId USER_ID = new UserId(456L);
    private static final String FIRST_NAME = "First name";
    private static final Dice DICE = null;
    private static final MessageId FORWARDED = null;

    private static final String GAME_NAME = "cube-game";

    @AfterEach
    void cleanUp() {
        final MongoDatabase database = mongoClient.getDatabase("cube-game");
        database.drop();
    }

    @Test
    @DisplayName("Success when choose game")
    void successWhenChooseGame() {
        final Speech comment = speechFactory.of(GAME_NAME);
        final Message message = new Message(CHAT_ID, USER_ID, FIRST_NAME, comment, DICE, FORWARDED);

        CascadePhaseStepper.moveUp(eventHandler, message, commands);

        final List<ResponseMessage> responses = eventHandler.handle(message);

        final Game storedGame = gameRepository.getActive(CHAT_ID).get();
        final PhaseExecutor phaseExecutor = phaseExecutorFactory
                .newInstance(storedGame.getPhase(), message.getChatId()).get();

        assertFalse(responses.isEmpty());
        assertEquals(2, responses.size());

        final ResponseMessage chooseGameResponse = responses.get(0);
        assertEquals(message.getChatId(), chooseGameResponse.getChatId());
        assertEquals(ResponseType.TEXT, chooseGameResponse.getType());
        assertTrue(chooseGameResponse.getMessage().contains(message.getSpeech().getText()));

        final ResponseMessage nextPhaseIntro = responses.get(1);
        assertEquals(message.getChatId(), nextPhaseIntro.getChatId());
        assertEquals(ResponseType.TEXT, nextPhaseIntro.getType());

        phaseExecutor.inception()
                .ifPresent(responseMessage ->
                        assertEquals(nextPhaseIntro.getMessage(), responseMessage.getMessage())
                );
    }

    @Test
    @DisplayName("Skip if user is not owner")
    void skipMessageIfNotTagged() {
        final UserId anotherUserId = new UserId(ThreadLocalRandom.current().nextLong());

        final Message msgTemplate = new Message(CHAT_ID, USER_ID, FIRST_NAME, speechFactory.of(GAME_NAME), DICE, FORWARDED);

        final Speech speech = speechFactory.of(String.format("@%s %s", applicationProperties.getBotName(), GAME_NAME));
        final Message message = new Message(CHAT_ID, anotherUserId, FIRST_NAME, speech, DICE, FORWARDED);

        CascadePhaseStepper.moveUp(eventHandler, msgTemplate, commands);

        final List<ResponseMessage> responses = eventHandler.handle(message);

        assertTrue(responses.isEmpty());
    }

    final List<Reply> commands = Collections.singletonList(
            new Reply(USER_ID, speechFactory.of(String.format("%s@%s", Command.START.getValue(), BOT_NAME)))
    );

}