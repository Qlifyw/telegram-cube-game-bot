package org.cubegame.domain.exceptions;

public class Incident extends RuntimeException{

    IncidentType type;

    public Incident(final String s, IncidentType type) {
        super(s);
        this.type = type;
    }

}
