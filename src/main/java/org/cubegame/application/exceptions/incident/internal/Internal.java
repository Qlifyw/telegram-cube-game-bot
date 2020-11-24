package org.cubegame.application.exceptions.incident.internal;

public interface Internal {

    interface Database extends InternalErrorType {
        enum Data implements Database {
            MAPPING,
            PARSING
        }
    }

    enum Logical implements InternalErrorType {
        INCONSISTENCY
    }


    interface Network extends InternalErrorType {
        enum General implements Network {
            IO
        }

        enum Api implements Network {
            REST,
            RPC
        }
    }
}

