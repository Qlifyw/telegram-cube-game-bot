package org.cubegame.domain.events;

import org.cubegame.domain.exceptions.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Phase {
    EMPTY("empty"),
    CHOOSE_GAME("choose_the_game"),
    NUMBER_OF_PLAYERS("number_of_players"),
    AWAIT_PLAYERS("await_players");

    private final String value;
    private final static Map<String, Phase> enumsByValue = new HashMap<>();

    static {
        for (Phase item : values()) {
            enumsByValue.put(item.value.toUpperCase(), item);
        }
    }
    private Phase(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Phase fromValue(final String value) {
        final Phase createdValue = enumsByValue.get(value.toUpperCase());
        if (createdValue == null) {
            final List<String> allowedValues = Arrays.stream(values())
                    .map(Phase::getValue)
                    .collect(Collectors.toList());

            throw new EnumException(Command.class.getName(), value, allowedValues);
        }
        return createdValue;
    }

}
