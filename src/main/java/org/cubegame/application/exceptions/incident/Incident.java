package org.cubegame.application.exceptions.incident;

public abstract class Incident extends RuntimeException {

    final Exception cause;

    public Incident(final String description, final Exception cause) {
        super(description);
        this.cause = cause;
    }

}
