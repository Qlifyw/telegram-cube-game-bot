package org.cubegame.application.exceptions.fail.error;

import org.cubegame.application.exceptions.fail.Incident;

public class ExternalError extends Incident {

    private final ExternalErrorType type;

    public ExternalError(final String s, final ExternalErrorType type, final Exception cause) {
        super(s, cause);
        this.type = type;
    }

    public ExternalErrorType getType() {
        return type;
    }
}
