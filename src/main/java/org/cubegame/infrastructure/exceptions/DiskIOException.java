package org.cubegame.infrastructure.exceptions;


import org.cubegame.domain.exceptions.fail.incident.InternalError;
import org.cubegame.domain.exceptions.fail.incident.InternalErrorType;

public final class DiskIOException extends InternalError {

    public DiskIOException(final String message, final Exception cause) {
        super("IO Incident. " + message, InternalErrorType.DISK_IO_ERROR, cause);
    }
}
