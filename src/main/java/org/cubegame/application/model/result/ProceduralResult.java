package org.cubegame.application.model.result;

public class ProceduralResult implements PhaseResponse {

    @Override
    public ProcessingStatus getStatus() {
        return ProcessingStatus.PROCEDURAL;
    }
}
