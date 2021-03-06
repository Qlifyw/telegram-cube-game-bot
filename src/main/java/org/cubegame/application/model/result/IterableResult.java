package org.cubegame.application.model.result;

import org.cubegame.infrastructure.model.message.type.ResponseMessage;

public class IterableResult implements PhaseResponse {

    private final ResponseMessage responseMessage;

    public IterableResult(final ResponseMessage responseMessage) {
        this.responseMessage = responseMessage;
    }

    @Override
    public ProcessingStatus getStatus() {
        return ProcessingStatus.ITERABLE;
    }

    public ResponseMessage getResponseMessage() {
        return responseMessage;
    }
}
