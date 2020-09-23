package org.cubegame.application.handler;

import org.cubegame.application.model.IterableResult;
import org.cubegame.application.model.PhaseStatebleResponse;
import org.cubegame.application.model.SkipedResult;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.exceptions.GameNoFoundException;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.TextResponseMessage;
import org.cubegame.infrastructure.repository.game.GameRepository;
import org.telegram.telegrambots.meta.api.objects.Dice;

import java.util.Optional;

public class StartGamePhaseExecutor implements PhaseExecutor {

    private final ChatId chatId;
    private final GameRepository gameRepository;

    public StartGamePhaseExecutor(final ChatId chatId, final GameRepository gameRepository) {
        this.gameRepository = gameRepository;
        this.chatId = chatId;
    }

    @Override
    public Optional<ResponseMessage> initiation(ChatId chatId) {
        final TextResponseMessage initMessage = new TextResponseMessage(
                "Congratulation! Game is started",
                chatId
        );
        return Optional.of(initMessage);
    }

    @Override
    public PhaseStatebleResponse execute(Message message) {


        if (message.hasDice()) {
            final Dice dice = message.getDice();
            String firstName = message.getAuthor().getFirstName();

            final Game storedGame = gameRepository
                    .get(message.getChatId())
                    .orElseThrow(() -> new GameNoFoundException(message.getChatId()));


            return new IterableResult(
                    new TextResponseMessage(
                            String.format("User %s has %d point", firstName, dice.getValue()),
                            message.getChatId()
                    )
            );
        }

        return new SkipedResult();
    }

    @Override
    public Phase getPhase() {
        return Phase.STARTED;
    }

}
