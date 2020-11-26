package org.cubegame.infrastructure.repositories.game;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.cubegame.application.repositories.game.GameRepository;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.game.state.Phase;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.infrastructure.repositories.game.entity.GameEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static org.cubegame.infrastructure.repositories.utils.Transformations.toJson;
import static org.cubegame.infrastructure.repositories.utils.Transformations.toObject;

public class GameRepositoryImpl implements GameRepository {

    private static final Logger LOG = LoggerFactory.getLogger(GameRepositoryImpl.class);

    private static final String DATABASE_NAME = "cube-game";
    private static final String COLLECTION_NAME = "games";

    private final MongoCollection<Document> gamesCollection;

    public GameRepositoryImpl(final MongoClient mongoClient) {
        this.gamesCollection = mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);
    }


    @Override
    public Optional<Game> getActive(ChatId chatId) {
        final List<String> activePhases = activePhases().stream()
                .map(Phase::toString)
                .collect(Collectors.toList());

        final Document activeGame = gamesCollection
                .find(
                        and(
                                eq("chatId", chatId.toString()),
                                in("phaseProp", activePhases)
                        )
                )
                .first();

        if (activeGame == null)
            return Optional.empty();

        final GameEntity gameEntity = toObject(activeGame, GameEntity.class);
        final Game game = GameEntity.toDomain(gameEntity);

        return Optional.of(game);
    }

    @Override
    public void save(final Game game) {
        final GameEntity gameEntity = GameEntity.fromDomain(game);
        final String json = toJson(gameEntity);
        final Document document = Document.parse(json);
        gamesCollection.insertOne(document);
    }

    @Override
    public void update(final Game game) {
        final GameEntity gameEntity = GameEntity.fromDomain(game);
        final String json = toJson(gameEntity);
        final Document document = Document.parse(json);


        final Bson filter = and(
                eq("chatId", game.getChatId().toString()),
                eq("gameId", game.getGameId().toString())
        );

        final Object replacedObject = gamesCollection.findOneAndReplace(filter, document);
        if (replacedObject == null) {
            LOG.debug("Nothing to replace. Maybe use save() instead.");
        } else {
            final Document replacedDocument = gamesCollection.findOneAndReplace(filter, document);
            LOG.debug("Replaced document: {}", replacedDocument);
        }
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
}
