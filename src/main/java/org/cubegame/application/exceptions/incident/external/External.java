package org.cubegame.application.exceptions.incident.external;


public interface External {

    interface Request extends ExternalErrorType {
        enum Data {
            MAPPING,
            PARSING
        }
    }

    enum Logical implements ExternalErrorType {
        INCONSISTENCY
    }
}

