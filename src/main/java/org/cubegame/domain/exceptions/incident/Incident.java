package org.cubegame.domain.exceptions.incident;

public class Incident extends RuntimeException{

    final IncidentType type;
    final Exception cause;

    public Incident(final String s, final IncidentType type, final Exception cause) {
        super(s);
        this.type = type;
        this.cause = cause;
    }

}
