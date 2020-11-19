package org.cubegame.application.exceptions.incident.external;

import org.cubegame.application.exceptions.incident.Incident;

import java.util.Map;

public class ExternalError extends Incident {

    private final ExternalErrorType type;

    public ExternalError(final String description, final ExternalErrorType type, final Map<String, Object> metadata, final Exception reason) {
        super(description, metadata, reason);
        this.type = type;
    }

    public ExternalErrorType getType() {
        return type;
    }
}
