package org.cubegame.application.model;

public class SkipedResult implements PhaseStatebleResponse {

    @Override
    public ProcessingStatus getStatus() {
        return ProcessingStatus.SKIPPED;
    }
}
