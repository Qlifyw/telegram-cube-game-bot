package org.cubegame.application.model;

import org.cubegame.infrastructure.model.message.ResponseMessage;

public final class FailedResult implements PhaseStatebleResponse {

    private final ResponseMessage responseMessage;

    public FailedResult(final ResponseMessage responseMessage) {
        this.responseMessage = responseMessage;
    }

    @Override
    public ProcessingStatus getStatus() {
        return ProcessingStatus.FAILED;
    }

    public ResponseMessage getResponseMessage() {
        return responseMessage;
    }
}
