package org.cubegame.application.model;

public class ProceduralResult implements PhaseStatebleResponse {

    @Override
    public ProcessingStatus getStatus() {
        return ProcessingStatus.PROCEDURAL;
    }
}
