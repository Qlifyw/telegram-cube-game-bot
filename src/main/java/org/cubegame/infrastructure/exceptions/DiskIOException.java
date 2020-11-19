package org.cubegame.infrastructure.exceptions;


import org.cubegame.application.exceptions.incident.internal.InternalError;
import org.cubegame.application.exceptions.incident.internal.InternalErrorType;

import java.util.Collections;

public final class DiskIOException extends InternalError {

    public DiskIOException(final String message, final Exception cause) {
        super(InternalErrorType.DISK_IO_ERROR, "IO Incident. " + message, Collections.emptyMap(), cause);
    }
}
