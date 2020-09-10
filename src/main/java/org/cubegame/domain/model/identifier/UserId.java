package org.cubegame.domain.model.identifier;

public final class UserId {
    private final long value;

    public UserId(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
