package org.cubegame.application.model;

public class ProceduralResult implements PhaseResponse {

    @Override
    public ProcessingStatus getStatus() {
        return ProcessingStatus.PROCEDURAL;
    }
}
