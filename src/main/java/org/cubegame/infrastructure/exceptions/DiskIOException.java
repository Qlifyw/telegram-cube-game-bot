package org.cubegame.infrastructure.exceptions;


import org.cubegame.application.exceptions.incident.internal.InternalError;
import org.cubegame.application.exceptions.incident.internal.InternalErrorType;

public final class DiskIOException extends InternalError {

    public DiskIOException(final String message, final Exception cause) {
        super("IO Incident. " + message, InternalErrorType.DISK_IO_ERROR, cause);
    }
}
