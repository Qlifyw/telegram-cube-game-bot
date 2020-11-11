package org.cubegame.infrastructure.exceptions;

import org.cubegame.application.exceptions.incident.internal.InternalError;
import org.cubegame.application.exceptions.incident.internal.InternalErrorType;
import org.cubegame.domain.model.identifier.ChatId;

public final class GameNoFoundException extends InternalError {

    public GameNoFoundException(final ChatId chatId) {
        super(
                InternalErrorType.GAME_NOT_FOUND,
                String.format("Cannot find game session for chat with id '%d'", chatId.getValue()),
                null
        );
    }
}
