package org.cubegame.application.handler;

import org.cubegame.application.model.PhaseStatusable;
import org.cubegame.application.model.ProcessedResult;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.TextResponseMessage;
import org.cubegame.infrastructure.repository.game.GameRepository;
import org.telegram.telegrambots.meta.api.objects.Dice;

import java.util.Optional;

public class StartGamePhaseExecutor implements PhaseExecutor {

    @Override
    public Optional<ResponseMessage> initiation(ChatId chatId) {
        final TextResponseMessage initMessage = new TextResponseMessage(
                "Confradulation! Game is started",
                chatId
        );
        return Optional.of(initMessage);
    }

    @Override
    public PhaseStatusable execute(Message message, GameRepository gameRepository) {

        if (message.hasDice()) {
            final Dice dice = message.getDice();
            String firstName = message.getAuthor().getFirstName();

            return new ProcessedResult(
                    new TextResponseMessage(
                            String.format("User %s has %d point", firstName, dice.getValue()),
                            message.getChatId()
                    )
            );
        }

        return new ProcessedResult(
                new TextResponseMessage(
                        "Confradulation! Game is started",
                        message.getChatId()
                )
        );
    }

    @Override
    public Phase getPhase() {
        return Phase.STARTED;
    }

}
