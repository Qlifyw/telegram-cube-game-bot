package org.cubegame.application.model.result;

import org.cubegame.infrastructure.model.message.type.ResponseMessage;

public final class FailedResult implements PhaseResponse {

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
