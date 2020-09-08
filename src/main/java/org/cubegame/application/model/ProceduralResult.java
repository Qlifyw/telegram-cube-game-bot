package org.cubegame.application.model;

public class ProceduralResult implements PhaseStatusable {

    @Override
    public ProcessingStatus getStatus() {
        return ProcessingStatus.PROCEDURAL;
    }
}
