package org.cubegame.application.executors.phase;

import org.cubegame.application.executors.factory.PhaseExecutor;
import org.cubegame.application.model.FailedResult;
import org.cubegame.application.model.PhaseResponse;
import org.cubegame.application.model.ProcessedResult;
import org.cubegame.application.model.SkipedResult;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.game.GameBuilder;
import org.cubegame.domain.model.game.state.Phase;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.exceptions.GameNoFoundException;
import org.cubegame.infrastructure.model.message.ErrorResponseMessage;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.TextResponseMessage;
import org.cubegame.infrastructure.repositories.game.GameRepository;

import java.util.Optional;

public class PlayersAmountPhaseExecutor implements PhaseExecutor {

    private final ChatId chatId;
    private final GameRepository gameRepository;

    public PlayersAmountPhaseExecutor(final ChatId chatId, final GameRepository gameRepository) {
        this.gameRepository = gameRepository;
        this.chatId = chatId;
    }

    @Override
    public Optional<ResponseMessage> initiation() {
        final TextResponseMessage initMessage = new TextResponseMessage("Specify players amount", chatId);
        return Optional.of(initMessage);
    }

    @Override
    public PhaseResponse execute(Message message) {
        switch (message.getSpeech().getType()) {
            case COMMENT:
                return new SkipedResult();
            case APEAL:
                break;
        }

        final Game storedGame = gameRepository
                .getActive(message.getChatId())
                .orElseThrow(() -> new GameNoFoundException(message.getChatId()));

        if (!message.getAuthor().getUserId().equals(storedGame.getOwner()))
            return new SkipedResult();

        final int numberOfPlayers;
        try {
            numberOfPlayers = Integer.parseInt(message.getSpeech().getText());
        } catch (NumberFormatException exception) {
            return new FailedResult(
                    new ErrorResponseMessage(
                            "Invalid number of players. Please enter integer value.",
                            message.getChatId()
                    )
            );
        }
        if (numberOfPlayers <= 0) {
            return new FailedResult(
                    new ErrorResponseMessage(
                            "Invalid number of players. Number must be greater than 0.",
                            message.getChatId()
                    )
            );
        }

        final Phase nextPhase = Phase.getNextFor(getPhase());

        final Game updatedGame = GameBuilder.from(storedGame)
                .setPhase(nextPhase)
                .setNumberOfPlayers(numberOfPlayers)
                .build();
        gameRepository.save(updatedGame);

        return new ProcessedResult(
                new TextResponseMessage(
                        String.format("Await for %d players", updatedGame.getNumberOfPlayers()),
                        message.getChatId()
                )
        );
    }

    @Override
    public Phase getPhase() {
        return Phase.NUMBER_OF_PLAYERS;
    }
}
