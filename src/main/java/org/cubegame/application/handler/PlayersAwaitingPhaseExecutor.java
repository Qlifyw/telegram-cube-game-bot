package org.cubegame.application.handler;

import org.cubegame.application.model.IterableResult;
import org.cubegame.application.model.PhaseResponse;
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
import org.cubegame.infrastructure.repositories.game.GameRepository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class PlayersAwaitingPhaseExecutor implements PhaseExecutor {

    private final ChatId chatId;
    private final GameRepository gameRepository;
    private final Game storedGame;

    private final Set<Player> awaitedPlayers = new LinkedHashSet<>();

    PlayersAwaitingPhaseExecutor(final ChatId chatId, final GameRepository gameRepository) {
        this.gameRepository = gameRepository;
        this.chatId = chatId;
        this.storedGame = this.gameRepository
                .getActive(this.chatId)
                .orElseThrow(() -> new GameNoFoundException(this.chatId));
    }

    @Override
    public Optional<ResponseMessage> initiation() {
        return Optional.empty();
    }

    @Override
    public PhaseResponse execute(Message message) {
        switch (message.getSpeech().getType()) {
            case COMMENT:
                return new SkipedResult();
            case APEAL:
                break;
        }

        final Player newPlayer = new Player(message.getAuthor().getUserId(), message.getAuthor().getFirstName());
        if (awaitedPlayers.contains(newPlayer))
            return new SkipedResult();

        awaitedPlayers.add(newPlayer);

        if (awaitedPlayers.size() == storedGame.getNumberOfPlayers()) {

            final Phase nextPhase = Phase.getNextFor(getPhase());
            final Game updatedGame = GameBuilder.from(storedGame)
                    .setPlayers(new ArrayList<>(awaitedPlayers))
                    .setPhase(nextPhase)
                    .build();

            gameRepository.save(updatedGame);
            return new ProceduralResult();
        } else {
            return new IterableResult(
                    new TextResponseMessage(
                            String.format("Await for %d players", storedGame.getNumberOfPlayers() - awaitedPlayers.size()),
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
