package org.cubegame.application.model.result;

public class SkipedResult implements PhaseResponse {

    @Override
    public ProcessingStatus getStatus() {
        return ProcessingStatus.SKIPPED;
    }
}
