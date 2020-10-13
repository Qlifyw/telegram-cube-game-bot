package org.cubegame.application.exceptions.fail.incident;

public enum InternalErrorType {

    GAME_NOT_FOUND("1"),
    DISK_IO_ERROR("2");

    private final String value;
    private static final String PREFIX = "IE";

    InternalErrorType(final String code) {
        this.value = PREFIX + code;
    }

    public String getValue() {
        return value;
    }
}
