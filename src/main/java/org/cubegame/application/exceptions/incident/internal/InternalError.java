package org.cubegame.application.exceptions.incident.internal;

import org.cubegame.application.exceptions.incident.Incident;
import org.cubegame.domain.utils.PrintFormaters;

import java.util.Collections;
import java.util.Map;

public class InternalError extends Incident {

    private final InternalErrorType type;

    public InternalError(final InternalErrorType type, final String description, final Map<String, Object> metadata, final Exception reason) {
        super(description, metadata, reason);
        this.type = type;
    }

    public InternalError(final InternalErrorType type, final String description, final Map<String, Object> metadata) {
        super(description, metadata, null);
        this.type = type;
    }

    public InternalError(final InternalErrorType type, final String description) {
        super(description, Collections.emptyMap(), null);
        this.type = type;
    }

    public InternalErrorType getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("%s. %s. %s", type, description, PrintFormaters.pretty(metadata));
    }
}
