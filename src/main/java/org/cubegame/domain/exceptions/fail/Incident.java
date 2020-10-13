package org.cubegame.domain.exceptions.fail;

public class Incident extends RuntimeException {

    final Exception cause;

    public Incident(final String s, final Exception cause) {
        super(s);
        this.cause = cause;
    }

}
