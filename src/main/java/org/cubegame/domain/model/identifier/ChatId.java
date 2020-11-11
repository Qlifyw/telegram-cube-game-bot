package org.cubegame.domain.model.identifier;

import java.util.Objects;

public final class ChatId {

    // TODO use primitive
    private final Long value;

    public ChatId(Long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ChatId chatId = (ChatId) o;
        return Objects.equals(value, chatId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
