package org.cubegame.application.exceptions.incident.external;

import org.cubegame.application.exceptions.incident.Incident;
import org.cubegame.domain.utils.PrintFormaters;

import java.util.Collections;
import java.util.Map;

public class ExternalError extends Incident {

    private final ExternalErrorType type;

    public ExternalError(final String description, final ExternalErrorType type, final Map<String, Object> metadata, final Exception reason) {
        super(description, metadata, reason);
        this.type = type;
    }

    public ExternalError(final String description, final ExternalErrorType type, final Map<String, Object> metadata) {
        super(description, metadata, null);
        this.type = type;
    }

    public ExternalError(final String description, final ExternalErrorType type) {
        super(description, Collections.emptyMap(), null);
        this.type = type;
    }

    public ExternalErrorType getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("%s. %s. %s", type, description, PrintFormaters.pretty(metadata));
    }
}
