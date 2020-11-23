package org.cubegame.infrastructure.repositories.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.cubegame.application.exceptions.incident.internal.Internal;
import org.cubegame.application.exceptions.incident.internal.InternalError;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.game.state.Phase;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.infrastructure.repositories.game.entity.GameEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

public class GameRepositoryImpl implements GameRepository {

    private static final Logger LOG = LoggerFactory.getLogger(GameRepositoryImpl.class);

    // TODO delete comments
//    private Map<ChatId, Map<GameId, Game>> games = new LinkedHashMap();

    private static final String DATABASE_NAME = "cube-game";
    private static final String COLLECTION_NAME = "games";

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> gamesCollection;

    private final ObjectMapper objectMapper;

    public GameRepositoryImpl(
            final MongoClient mongoClient,
            final ObjectMapper objectMapper
    ) {

        this.mongoClient = mongoClient;
        this.database = mongoClient.getDatabase(DATABASE_NAME);
        this.gamesCollection = database.getCollection(COLLECTION_NAME);

        this.objectMapper = objectMapper;
    }


    @Override
    public Optional<Game> getActive(ChatId chatId) {
//        final Map<GameId, Game> gamesForChat = games
//                .get(chatId);

        final List<String> activePhases = activePhases().stream()
                .map(Phase::toString)
                .collect(Collectors.toList());

        final Document activeGame = gamesCollection
                .find(
                        and(
                                eq("chatIdProp", chatId.toString()),
                                in("phaseProp", activePhases)
                        )
                )
                .first();

        if (activeGame == null)
            return Optional.empty();

//        List<Game> activeGames =
//                gamesForChat.values().stream()
//                        .filter(game -> isActiveGame(game.getPhase()))
//                        .collect(Collectors.toList());

        final GameEntity gameEntity = toObject(activeGame, GameEntity.class);
        final Game game = GameEntity.toDomain(gameEntity);

        return Optional.of(game);

    }

    @Override
    public void save(final Game game) {
        final Function<GameEntity, String> getGameId = GameEntity::getGameId;

        final GameEntity gameEntity = GameEntity.fromDomain(game);
        final String json = toJson(gameEntity);
        final Document document = Document.parse(json);
        gamesCollection.insertOne(document);

//        if (chatGames == null) {
//            final Map<GameId, Game> gamesForChat = new LinkedHashMap<>();
//            gamesForChat.put(game.getGameId(), game);
//            this.games.put(game.getChatId(), gamesForChat);
//        } else {
//            chatGames.put(game.getGameId(), game);
//            this.games.put(game.getChatId(), chatGames);
//        }
    }

    @Override
    public void update(final Game game) {
        final GameEntity gameEntity = GameEntity.fromDomain(game);
        final String json = toJson(gameEntity);
        final Document document = Document.parse(json);


        final Bson filter = and(
                eq("chatIdProp", game.getChatId().toString()),
                eq("gameIdProp", game.getGameId().toString())
        );

        final Object replacedObject = gamesCollection.findOneAndReplace(filter, document);
        if (replacedObject == null) {
            LOG.debug("Nothing to replace. Maybe use save() instead.");
        } else {
            final Document replacedDocument = (Document) gamesCollection.findOneAndReplace(filter, document);
            LOG.debug("Replaced document: " + replacedDocument);
        }


//        if (chatGames == null) {
//            final Map<GameId, Game> gamesForChat = new LinkedHashMap<>();
//            gamesForChat.put(game.getGameId(), game);
//            this.games.put(game.getChatId(), gamesForChat);
//        } else {
//            chatGames.put(game.getGameId(), game);
//            this.games.put(game.getChatId(), chatGames);
//        }
    }


    private List<Phase> activePhases() {
        return Arrays.stream(Phase.values())
                .filter(this::isActiveGame)
                .collect(Collectors.toList());
    }

    private boolean isActiveGame(Phase phase) {
        switch (phase) {
            case EMPTY:
            case CHOOSE_GAME:
            case NUMBER_OF_ROUNDS:
            case NUMBER_OF_PLAYERS:
            case AWAIT_PLAYERS:
            case STARTED:
                return true;

            case CANCELED:
            case COMPLETED:
                return false;
        }

        return false;
    }


    private <T> String toJson(T object) {
        final String json;
        try {
            json = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new InternalError(
                    Internal.Database.MAPPING,
                    "Cannot serialize object.",
                    Collections.emptyMap(),
                    null
            );
        }
        return json;
    }

    private <T> T toObject(Document document, Class<T> clazz) {
        final T object;
        try {
            object = objectMapper.readValue(document.toJson(), clazz);
        } catch (JsonProcessingException e) {

            // TODO add MDC
            throw new InternalError(
                    Internal.Database.PARSING,
                    "Cannot deserialize object.",
                    Collections.emptyMap(),
                    null
            );
        }
        return object;
    }
}
