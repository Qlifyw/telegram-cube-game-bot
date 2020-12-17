package org.cubegame.domain.model.round;

import java.util.Objects;

public class Points implements Comparable<Points> {
    private final int amount;

    public Points(final int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public int compareTo(final Points p) {
        return Integer.compare(amount, p.amount);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Points points = (Points) o;
        return amount == points.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}
