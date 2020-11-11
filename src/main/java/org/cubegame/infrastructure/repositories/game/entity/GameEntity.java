package org.cubegame.infrastructure.repositories.game.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.game.state.Phase;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.GameId;
import org.cubegame.domain.model.identifier.UserId;
import org.cubegame.infrastructure.repositories.round.entity.PlayerEntity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


 // TODO remove annotation and add propery object ID
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameEntity {

    @JsonProperty("gameIdProp")
    private final String gameId;

    @JsonProperty("chatIdProp")
    private final String chatId;

    @JsonProperty("gameNameProp")
    private final String gameName;

    @JsonProperty("numberOfPlayerProp")
    private final int numberOfPlayers;

    @JsonProperty("numberOfRoundsProp")
    private final int numberOfRounds;

    @JsonProperty("phaseProp")
    private final String phase;

    @JsonProperty("playersProp")
    private final List<PlayerEntity> players;

    @JsonProperty("ownerProp")
    private final String owner;


    @JsonCreator
    public GameEntity(
            @JsonProperty("gameIdProp")
            final String gameId,

            @JsonProperty("chatIdProp")
            final String chatId,

            @JsonProperty("phaseProp")
            final String phase,

            @JsonProperty("gameNameProp")
            final String gameName,

            @JsonProperty("numberOfPlayerProp")
            final int numberOfPlayers,

            @JsonProperty("numberOfRoundsProp")
            final int numberOfRounds,

            @JsonProperty("playersProp")
            final List<PlayerEntity> players,

            @JsonProperty("ownerProp")
            final String owner
    ) {
        this.gameId = gameId;
        this.chatId = chatId;
        this.phase = phase;
        this.gameName = gameName;
        this.numberOfPlayers = numberOfPlayers;
        this.numberOfRounds = numberOfRounds;
        this.players = players;
        this.owner = owner;
    }

    public String getGameId() {
        return gameId;
    }

    public String getChatId() {
        return chatId;
    }

    public String getGameName() {
        return gameName;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public String getPhase() {
        return phase;
    }

    public List<PlayerEntity> getPlayers() {
        return players;
    }

    public String getOwner() {
        return owner;
    }

    public static GameEntity fromDomain(Game game) {
        return new GameEntity(
                game.getGameId().toString(),
                game.getChatId().toString(),
                game.getPhase().toString(),
                game.getGameName(),
                game.getNumberOfPlayers(),
                game.getNumberOfRounds(),
                game.getPlayers()
                        .stream()
                        .map(PlayerEntity::fromDomain)
                        .collect(Collectors.toList()),
                game.getOwner().toString()
        );
    }

    public static Game toDomain(GameEntity gameEntity) {
        return new Game(
                new GameId(UUID.fromString(gameEntity.getGameId())),
                new ChatId(Long.valueOf(gameEntity.getChatId())),
                Phase.fromString(gameEntity.getPhase()),
                gameEntity.getGameName(),
                gameEntity.getNumberOfPlayers(),
                gameEntity.getNumberOfRounds(),
                gameEntity.getPlayers()
                        .stream()
                        .map(PlayerEntity::toDomain)
                        .collect(Collectors.toList()),
                new UserId(Long.parseLong(gameEntity.getOwner()))
        );
    }

}
