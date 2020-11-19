package org.cubegame.application.exceptions.incident;

import java.util.Map;

public abstract class Incident extends RuntimeException {

    protected final Exception reason;
    protected final String description;
    protected final Map<String, Object> metadata;

    public Incident(final String description, final Map<String, Object> metadata, final Exception reason) {
        super(description);
        this.description = description;
        this.metadata = metadata;
        this.reason = reason;
    }

    public Exception getReason() {
        return reason;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
