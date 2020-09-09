package org.cubegame.application.handler;

import org.cubegame.application.model.PhaseStatebleResponse;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.repository.game.GameRepository;

import java.util.Optional;

// TODO abstract class
public interface PhaseExecutor {

    Optional<ResponseMessage> initiation(ChatId chatId);

    PhaseStatebleResponse execute(Message message, GameRepository gameRepository);

    Phase getPhase();
}
