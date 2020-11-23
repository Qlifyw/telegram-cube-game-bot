package org.cubegame.application.exceptions.incident.external;


public interface External {

    enum Request implements ExternalErrorType {
        MAPPING,
        PARSING
    }

    enum Logical implements ExternalErrorType {
        INCONSISTENCY
    }
}

