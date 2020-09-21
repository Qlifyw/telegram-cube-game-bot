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
import java.util.List;
import java.util.Optional;

public class PlayersAwaitingPhaseExecutor implements PhaseExecutor {

    final List<Player> awaitedPlayers = new ArrayList<>();

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

        final Player newPlayer = new Player(message.getAuthor().getUserId());
        if (storedGame.getPlayers().contains(newPlayer))
            return new SkipedResult();

        awaitedPlayers.add(newPlayer);

        if (awaitedPlayers.size() == storedGame.getNumerOfPlayers()) {
            final ArrayList<Player> currentPlayers = new ArrayList<>(storedGame.getPlayers());
            final List<Player> updatedPlayers = new ArrayList<>(CollectionUtils.union(currentPlayers, awaitedPlayers));

            final Phase nextPhase = Phase.getNextFor(getPhase());
            final Game updatedGame = GameBuilder.from(storedGame)
                    .setPlayers(updatedPlayers)
                    .setPhase(nextPhase)
                    .build();

            gameRepository.save(updatedGame);
            return new ProceduralResult();
        } else {
//            gameRepository.save(currentGameBuilder.build());
            System.out.println("save in db");
            return new IterableResult(
                    new TextResponseMessage(
                            String.format("Await for %d players", storedGame.getNumerOfPlayers() - awaitedPlayers.size()),
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
