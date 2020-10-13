package org.cubegame.infrastructure.exceptions;

import org.cubegame.application.exceptions.incident.internal.InternalError;
import org.cubegame.application.exceptions.incident.internal.InternalErrorType;
import org.telegram.telegrambots.meta.api.objects.Update;

public final class NotSupportedEventException extends InternalError {

    public NotSupportedEventException(final Update update) {
        super("Not supported event. " + update, InternalErrorType.GAME_NOT_FOUND, null);
    }
}
