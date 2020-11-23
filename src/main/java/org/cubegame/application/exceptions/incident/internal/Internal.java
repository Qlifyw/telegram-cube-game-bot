package org.cubegame.application.exceptions.incident.internal;

public interface Internal {

    enum Database implements InternalErrorType {
        MAPPING,
        PARSING
    }

    enum Logical implements InternalErrorType {
        INCONSISTENCY
    }


    interface Network extends InternalErrorType {
        Network IO = Io.IO;

        enum Io implements Network {
            REST
        }

        enum Api implements Network {
            REST,
            RPC
        }
    }
}

