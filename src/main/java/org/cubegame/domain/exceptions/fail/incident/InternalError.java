package org.cubegame.domain.exceptions.fail.incident;

import org.cubegame.domain.exceptions.fail.Incident;

public class InternalError extends Incident {

    private final InternalErrorType type;

    public InternalError(final String s, final InternalErrorType type, final Exception cause) {
        super(s, cause);
        this.type = type;
    }

    public InternalErrorType getType() {
        return type;
    }
}
