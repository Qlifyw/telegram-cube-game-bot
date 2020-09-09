package org.cubegame.application.model;

import org.cubegame.infrastructure.model.message.ResponseMessage;

public class IterableResult implements PhaseStatebleResponse {

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
