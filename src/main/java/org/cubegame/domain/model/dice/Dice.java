package org.cubegame.domain.model.dice;

public class Dice {

    private final int value;

    public Dice(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Dice{" +
                "value=" + value +
                '}';
    }
}
