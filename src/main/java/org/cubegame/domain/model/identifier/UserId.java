package org.cubegame.domain.model.identifier;

import java.util.Objects;

public final class UserId {
    private final long value;

    public UserId(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final UserId userId = (UserId) o;
        return value == userId.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
