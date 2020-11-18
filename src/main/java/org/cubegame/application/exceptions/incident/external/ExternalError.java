package org.cubegame.application.exceptions.incident.external;

import org.cubegame.application.exceptions.incident.Incident;

public class ExternalError extends Incident {

    private final ExternalErrorType type;

    public ExternalError(final String description, final ExternalErrorType type, final Exception cause) {
        super(description, cause);
        this.type = type;
    }

    public ExternalErrorType getType() {
        return type;
    }
}
