package org.cubegame.infrastructure.exceptions;

import org.cubegame.application.exceptions.fail.incident.InternalError;
import org.cubegame.application.exceptions.fail.incident.InternalErrorType;
import org.telegram.telegrambots.meta.api.objects.Update;

public final class NotSupportedEventException extends InternalError {

    public NotSupportedEventException(final Update update) {
        super("Not supported event. " + update, InternalErrorType.GAME_NOT_FOUND, null);
    }
}
