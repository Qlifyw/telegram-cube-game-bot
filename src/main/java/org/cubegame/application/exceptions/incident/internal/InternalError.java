package org.cubegame.application.exceptions.incident.internal;

import org.cubegame.application.exceptions.incident.Incident;

public class InternalError extends Incident {

    private final InternalErrorType type;

    public InternalError(final InternalErrorType type, final String description, final Exception cause) {
        super(description, cause);
        this.type = type;
    }

    public InternalErrorType getType() {
        return type;
    }
}
