package org.cubegame.domain.exceptions;

import org.cubegame.domain.exceptions.incident.Incident;
import org.cubegame.domain.exceptions.incident.IncidentType;

public final class DiskIOException extends Incident {

    public DiskIOException(final String message, final Exception cause) {
        super("IO Incident. " + message, IncidentType.DISK_IO_ERROR, cause);
    }
}
