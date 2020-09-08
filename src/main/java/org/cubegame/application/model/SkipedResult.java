package org.cubegame.application.model;

public class SkipedResult implements PhaseStatusable {

    @Override
    public ProcessingStatus getStatus() {
        return ProcessingStatus.SKIPPED;
    }
}
