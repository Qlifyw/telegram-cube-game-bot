package org.cubegame.application.model;

import org.cubegame.infrastructure.model.message.ResponseMessage;

// TODO final
public class ProcessedResult implements PhaseStatusable {

    private final ResponseMessage responseMessage;

    public ProcessedResult(final ResponseMessage responseMessage) {
        this.responseMessage = responseMessage;
    }

    @Override
    public ProcessingStatus getStatus() {
        return ProcessingStatus.PROCESSED;
    }

    public ResponseMessage getResponseMessage() {
        return responseMessage;
    }
}
