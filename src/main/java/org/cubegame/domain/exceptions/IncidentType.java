package org.cubegame.domain.exceptions;

public enum IncidentType {

    GAME_NOT_FOUND("1");

    private final String value;
    private static final String PREFIX = "INC";

    private IncidentType(final String code) {
        this.value = PREFIX + code;
    }

    public String getValue() {
        return value;
    }
}
