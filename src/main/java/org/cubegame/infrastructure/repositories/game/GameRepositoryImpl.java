package org.cubegame.infrastructure.repositories.game;

import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.game.state.Phase;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.GameId;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameRepositoryImpl implements GameRepository {

    private Map<ChatId, Map<GameId, Game>> games = new LinkedHashMap();

    @Override
    public Optional<Game> getActive(ChatId chatId) {
        final Map<GameId, Game> gamesForChat = games
                .get(chatId);

        if (gamesForChat == null)
            return Optional.empty();

        List<Game> activeGames =
                gamesForChat.values().stream()
                        .filter(game -> isActiveGame(game.getPhase()))
                        .collect(Collectors.toList());

        if (activeGames.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(activeGames.get(0));
        }
    }

    @Override
    public void save(final Game game) {
        final Map<GameId, Game> chatGames = this.games.get(game.getChatId());

        if (chatGames == null) {
            final Map<GameId, Game> gamesForChat = new LinkedHashMap<>();
            gamesForChat.put(game.getGameId(), game);
            this.games.put(game.getChatId(), gamesForChat);
        } else {
            chatGames.put(game.getGameId(), game);
            this.games.put(game.getChatId(), chatGames);
        }
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
