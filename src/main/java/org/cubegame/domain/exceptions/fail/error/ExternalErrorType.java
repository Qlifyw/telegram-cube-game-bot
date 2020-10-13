package org.cubegame.domain.exceptions.fail.error;

public enum ExternalErrorType {

    GAME_NOT_FOUND2("1");

    private final String value;
    private static final String PREFIX = "EE";

    ExternalErrorType(final String code) {
        this.value = PREFIX + code;
    }

    public String getValue() {
        return value;
    }
}
