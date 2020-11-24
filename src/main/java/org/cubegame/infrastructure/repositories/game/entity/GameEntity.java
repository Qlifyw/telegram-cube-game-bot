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

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameEntity {

    @JsonProperty("gameId")
    private final String gameId;

    @JsonProperty("chatId")
    private final String chatId;

    @JsonProperty("gameName")
    private final String gameName;

    @JsonProperty("numberOfPlayer")
    private final int numberOfPlayers;

    @JsonProperty("numberOfRounds")
    private final int numberOfRounds;

    @JsonProperty("phaseProp")
    private final String phase;

    @JsonProperty("players")
    private final List<PlayerEntity> players;

    @JsonProperty("owner")
    private final String owner;


    @JsonCreator
    public GameEntity(
            @JsonProperty("gameId") final String gameId,
            @JsonProperty("chatId") final String chatId,
            @JsonProperty("phaseProp") final String phase,
            @JsonProperty("gameName") final String gameName,
            @JsonProperty("numberOfPlayer") final int numberOfPlayers,
            @JsonProperty("numberOfRounds") final int numberOfRounds,
            @JsonProperty("players") final List<PlayerEntity> players,
            @JsonProperty("owner") final String owner
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
