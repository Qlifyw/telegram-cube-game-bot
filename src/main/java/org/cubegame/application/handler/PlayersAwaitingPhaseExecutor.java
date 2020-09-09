package org.cubegame.application.handler;

import org.apache.commons.collections4.CollectionUtils;
import org.cubegame.application.model.IterableResult;
import org.cubegame.application.model.PhaseStatebleResponse;
import org.cubegame.application.model.ProceduralResult;
import org.cubegame.application.model.SkipedResult;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.exceptions.GameNoFoundException;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.game.GameBuilder;
import org.cubegame.domain.model.game.Player;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.TextResponseMessage;
import org.cubegame.infrastructure.repository.game.GameRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PlayersAwaitingPhaseExecutor implements PhaseExecutor {

    @Override
    public Optional<ResponseMessage> initiation(final ChatId chatId) {
        return Optional.empty();
    }

    @Override
    public PhaseStatebleResponse execute(Message message, GameRepository gameRepository) {
        switch (message.getSpeech().getType()) {
            case COMMENT:
                return new SkipedResult();
            case APEAL:
                break;
        }

        final Game storedGame = gameRepository
                .get(message.getChatId())
                .orElseThrow(() -> new GameNoFoundException(message.getChatId()));

        final ArrayList<Player> currentPlayers = new ArrayList<>(storedGame.getPlayers());
        final List<Player> newPlayer = Collections.singletonList(new Player(message.getAuthor().getUserId()));
        final List<Player> updatedPlayers = new ArrayList<>(CollectionUtils.union(currentPlayers, newPlayer));

        final GameBuilder currentGameBuilder = GameBuilder.from(storedGame)
                .setPlayers(updatedPlayers);

        if (updatedPlayers.size() == storedGame.getNumerOfPlayers()) {
            final Phase nextPhase = Phase.getNextFor(getPhase());
            currentGameBuilder.setPhase(nextPhase);
            gameRepository.save(currentGameBuilder.build());
            return new ProceduralResult();
        } else {
            gameRepository.save(currentGameBuilder.build());
            return new IterableResult(
                    new TextResponseMessage(
                            String.format("Await for %d players", storedGame.getNumerOfPlayers() - updatedPlayers.size()),
                            message.getChatId()
                    )
            );
        }
    }

    @Override
    public Phase getPhase() {
        return Phase.AWAIT_PLAYERS;
    }
}
