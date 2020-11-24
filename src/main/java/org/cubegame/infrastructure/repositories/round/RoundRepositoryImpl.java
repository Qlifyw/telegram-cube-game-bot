package org.cubegame.infrastructure.repositories.round;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.cubegame.application.repositories.round.RoundRepository;
import org.cubegame.domain.model.identifier.GameId;
import org.cubegame.domain.model.round.Round;
import org.cubegame.infrastructure.repositories.round.entity.RoundEntity;

import java.util.Optional;

import static org.cubegame.infrastructure.repositories.utils.Transformations.toJson;

public class RoundRepositoryImpl implements RoundRepository {

    private static final String DATABASE_NAME = "cube-game";
    private static final String COLLECTION_NAME = "rounds";

    private final MongoCollection roundsCollection;

    public RoundRepositoryImpl(final MongoClient mongoClient) {
        this.roundsCollection = mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);
    }

    @Override
    public Optional<Round> get(final GameId gameId) {
        // TODO
        return Optional.empty();
    }

    @Override
    public void save(final Round round) {
        final RoundEntity roundEntity = RoundEntity.fromDomain(round);
        final String json = toJson(roundEntity);
        roundsCollection.insertOne(Document.parse(json));
    }
}
