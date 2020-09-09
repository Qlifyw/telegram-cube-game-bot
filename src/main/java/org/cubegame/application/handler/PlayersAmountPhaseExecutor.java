package org.cubegame.application.handler;

import org.cubegame.application.model.PhaseStatusable;
import org.cubegame.application.model.ProcessedResult;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.exceptions.GameNoFoundException;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.game.GameBuilder;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.model.message.ErrorResponseMessage;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.TextResponseMessage;
import org.cubegame.infrastructure.repository.game.GameRepository;

import java.util.Optional;

public class PlayersAmountPhaseExecutor implements PhaseExecutor {

    @Override
    public Optional<ResponseMessage> initiation(ChatId chatId) {
        final TextResponseMessage initMessage = new TextResponseMessage("Specify players amount", chatId);
        return Optional.of(initMessage);
    }

    @Override
    public PhaseStatusable execute(Message message, GameRepository gameRepository) {
        final int numberOfPlayers;
        try {
            numberOfPlayers = Integer.parseInt(message.getText());
        } catch (NumberFormatException exception) {
            return new ProcessedResult(
                    new ErrorResponseMessage(
                            "Invalid number of players. Please enter integer value.",
                            message.getChatId()
                    )
            );
        }
        if (numberOfPlayers <= 0) {
            return new ProcessedResult(
                    new ErrorResponseMessage(
                            "Invalid number of players. Number must be greater than 0.",
                            message.getChatId()
                    )
            );
        }

        final Game storedGame = gameRepository
                .get(message.getChatId())
                .orElseThrow(() -> new GameNoFoundException(message.getChatId()));

        final Phase nextPhase = Phase.getNextFor(getPhase());

        final Game updatedGame = GameBuilder.from(storedGame)
                .setPhase(nextPhase)
                .setNumerOfPlayers(numberOfPlayers)
                .build();
        gameRepository.save(updatedGame);

        return new ProcessedResult(
                new TextResponseMessage(
                        String.format("Await for %d players", updatedGame.getNumerOfPlayers()),
                        message.getChatId()
                )
        );
    }

    @Override
    public Phase getPhase() {
        return Phase.NUMBER_OF_PLAYERS;
    }
}
