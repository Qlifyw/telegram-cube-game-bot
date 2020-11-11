package org.cubegame.infrastructure.repositories.round;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.cubegame.application.exceptions.incident.internal.InternalError;
import org.cubegame.application.exceptions.incident.internal.InternalErrorType;
import org.cubegame.domain.model.identifier.GameId;
import org.cubegame.domain.model.round.Round;
import org.cubegame.infrastructure.repositories.round.entity.OutcomeEntity;
import org.cubegame.infrastructure.repositories.round.entity.PlayerEntity;
import org.cubegame.infrastructure.repositories.round.entity.PointsEntity;
import org.cubegame.infrastructure.repositories.round.entity.RoundEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoundRepositoryImpl implements RoundRepository {

    private static final String DATABASE_NAME = "cube-game";
    private static final String COLLECTION_NAME = "rounds";

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection roundsCollection;

    private final ObjectMapper objectMapper;

    public RoundRepositoryImpl(
            final MongoClient mongoClient,
            final ObjectMapper objectMapper
    ) {
        this.mongoClient = mongoClient;
        this.database = mongoClient.getDatabase(DATABASE_NAME);
        this.roundsCollection = database.getCollection(COLLECTION_NAME);

        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<Round> get(final GameId gameId) {
        // TODO
//        final FindIterable stored = roundsCollection.find().limit(1);
//        final Round stored;
        return Optional.empty();
    }

    @Override
    public void save(final Round round) {
        final RoundEntity roundEntity = RoundEntity.fromDomain(round);
        final String json = toJson(roundEntity);
        roundsCollection.insertOne(Document.parse(json));

//        final FindIterable<Document> findIterable = roundsCollection.find();
//        for (Document document : findIterable) {
//            System.out.println(document.toString());
//        }
    }

    public static void main(String[] args) {
//        final RoundRepositoryImpl roundRepository = new RoundRepositoryImpl();
//
        final List<OutcomeEntity> outcomes = new ArrayList<>();
        outcomes.add(
                new OutcomeEntity(
                        new PlayerEntity(656),
                        new PointsEntity(1)
                )
        );

        final RoundEntity round = new RoundEntity(outcomes, "some-related-game-id", "round-id");
//        roundRepository.save(round);


        final ObjectMapper mapper = new ObjectMapper();
        try {
            final String s = mapper.writeValueAsString(round);
            System.out.println(s);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


    }


    private <T> String toJson(T object) {
        final String json;
        try {
            json = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new InternalError(
                    InternalErrorType.JSON_MAPPING,
                    "Cannot deserialize object.",
                    null
            );
        }
        return json;
    }


}
