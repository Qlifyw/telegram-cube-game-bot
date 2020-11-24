package org.cubegame.infrastructure.bots;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.cubegame.application.exceptions.incident.Incident;
import org.cubegame.application.exceptions.incident.external.ExternalError;
import org.cubegame.application.exceptions.incident.internal.Internal;
import org.cubegame.application.exceptions.incident.internal.InternalError;
import org.cubegame.application.handler.EventHandler;
import org.cubegame.application.handler.EventHandlerImpl;
import org.cubegame.application.repositories.game.GameRepository;
import org.cubegame.application.repositories.round.RoundRepository;
import org.cubegame.domain.model.dice.Dice;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.UserId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.domain.model.message.speach.Speech;
import org.cubegame.domain.model.message.speach.SpeechFactory;
import org.cubegame.infrastructure.model.message.NavigationResponseMessage;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.TextualResponseMessage;
import org.cubegame.infrastructure.properties.ApplicationProperties;
import org.cubegame.infrastructure.repositories.game.GameRepositoryImpl;
import org.cubegame.infrastructure.repositories.round.RoundRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CubeGameBot extends TelegramLongPollingBot {

    private static final Logger LOG = LoggerFactory.getLogger(CubeGameBot.class);

    private final ApplicationProperties properties = ApplicationProperties.load();

    private final SpeechFactory speechFactory = new SpeechFactory(properties);

    private final ConnectionString connectionString = new ConnectionString(getConnectionUrl(properties));
    private final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .retryReads(true)
            .retryWrites(true)
            .build();

    private final MongoClient mongoClient = MongoClients.create(mongoClientSettings);


//    private final MongoCredential mongoCredential = MongoCredential.createCredential("mng-client", "cube-game", "mng-client-pass".toCharArray());
//    private final MongoClient mongoClient = new MongoClient (new ServerAddress("localhost", 27017), mongoCredential);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GameRepository gameRepository = new GameRepositoryImpl(mongoClient, objectMapper);
    private final RoundRepository roundRepository = new RoundRepositoryImpl(mongoClient, objectMapper);

    private final EventHandler eventHandler = new EventHandlerImpl(gameRepository, roundRepository, properties);

    public CubeGameBot() {
        final FindIterable<Document> games = mongoClient.getDatabase("cube-game").getCollection("games").find();
        games.forEach((Consumer<Document>) System.out::println);
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            receive(update);
        } catch (InternalError incident) {
            LOG.error("Internal error. " + incident.toString(), incident.getReason());
        } catch (ExternalError incident) {
            LOG.error("External error. " + incident.toString(), incident.getReason());
        } catch (Incident incident) {
            LOG.error("Unclassified error. " + incident.toString(), incident);
        }
    }

    public void receive(Update update) {
        final Message receivedMessage = extractMessageFromEvent(update)
                .orElseThrow(() -> notSupportedEventException(update));

        final List<ResponseMessage> responseMessages = eventHandler.handle(receivedMessage);

        for (ResponseMessage responseMessage : responseMessages) {
            switch (responseMessage.getType()) {
                case NAVIAGTION: {
                    final NavigationResponseMessage response = (NavigationResponseMessage) responseMessage;
                    showMenu(response.getMenu(), response.getChatId());
                    break;
                }
                case TEXT: {
                    final TextualResponseMessage response = (TextualResponseMessage) responseMessage;
                    respond(response);
                    break;
                }
            }
        }
    }

    public Optional<Message> extractMessageFromEvent(Update update) {

        // In case if the event is a usual message from the user
        if (update.hasMessage()) {
            final org.telegram.telegrambots.meta.api.objects.Message receivedMessage = update.getMessage();
            final String receivedText = receivedMessage.hasText() ? receivedMessage.getText() : "";
            final ChatId chatId = new ChatId(receivedMessage.getChatId());
            final UserId userId = new UserId(receivedMessage.getFrom().getId());
            final String firstName = receivedMessage.getFrom().getFirstName();
            final Dice dice = receivedMessage.hasDice() ? new Dice(receivedMessage.getDice().getValue()) : null;

            final Speech speech = speechFactory.of(receivedText);
            final Message createdMessage = new Message(chatId, userId, firstName, speech, dice);
            return Optional.of(createdMessage);
        }

        // In case if user interact with showed menu
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();

            final String receivedText = callbackQuery.getData();
            final ChatId chatId = new ChatId(callbackQuery.getMessage().getChat().getId());
            final UserId userId = new UserId(callbackQuery.getFrom().getId());
            final String firstName = callbackQuery.getMessage().getFrom().getFirstName();

            final Speech speech = speechFactory.of(receivedText);

            final Message createdMessage = new Message(chatId, userId, firstName, speech, null);
            return Optional.of(createdMessage);
        }

        return Optional.empty();
    }

    @Override
    public String getBotUsername() {
        return properties.getBotName();
    }

    @Override
    public String getBotToken() {
        return properties.getBotToken();
    }

    public void showMenu(InlineKeyboardMarkup menu, ChatId chatId) {
        SendMessage message = new SendMessage()
                .setChatId(chatId.getValue())
                .setText("Choose the game")
                .setReplyMarkup(menu);
        respondToClient(message);
    }

    public <T extends TextualResponseMessage> void respond(final T message) {
        final SendMessage responseMessage = new SendMessage()
                .setChatId(message.getChatId().getValue())
                .setText(message.getMessage());
        respondToClient(responseMessage);
    }

    private void respondToClient(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String getConnectionUrl(ApplicationProperties properties) {
        final String dbHost = properties.getDatabaseHost();
        final String dbPort = properties.getDatabasePort();
        final String dbUser = properties.getDatabaseUser();
        final String dbPass = properties.getDatabasePass();
        final String dbName = properties.getDatabaseName();

        return "mongodb://" + dbUser + ":" + dbPass + "@" + dbHost + ":" + dbPort + "/" + dbName;
    }


    private Incident notSupportedEventException(Update update) {
        return new InternalError(Internal.Logical.INCONSISTENCY, "Not supported event." + update);
    }
}
