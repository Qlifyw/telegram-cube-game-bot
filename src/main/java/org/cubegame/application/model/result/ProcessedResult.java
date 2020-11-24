package org.cubegame.application.model.result;

import org.cubegame.infrastructure.model.message.type.ResponseMessage;

public final class ProcessedResult implements PhaseResponse {

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
