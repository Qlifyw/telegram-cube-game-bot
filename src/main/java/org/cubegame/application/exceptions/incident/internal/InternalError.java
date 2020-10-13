package org.cubegame.application.exceptions.incident.internal;

import org.cubegame.application.exceptions.incident.Incident;

public class InternalError extends Incident {

    private final InternalErrorType type;

    public InternalError(final String s, final InternalErrorType type, final Exception cause) {
        super(s, cause);
        this.type = type;
    }

    public InternalErrorType getType() {
        return type;
    }
}
