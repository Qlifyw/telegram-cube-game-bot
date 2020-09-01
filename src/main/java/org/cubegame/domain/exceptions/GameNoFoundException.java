package org.cubegame.domain.exceptions;

import org.cubegame.domain.model.identifier.ChatId;

public class GameNoFoundException extends Incident {

    public GameNoFoundException(ChatId chatId) {
        super(
                String.format("Cannot find game session for chat with id '%d'", chatId.getValue()),
                IncidentType.GAME_NOT_FOUND
        );
    }
}
