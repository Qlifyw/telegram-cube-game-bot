package org.cubegame.application.model;

public class SkipedResult implements PhaseResponse {

    @Override
    public ProcessingStatus getStatus() {
        return ProcessingStatus.SKIPPED;
    }
}
