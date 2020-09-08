package org.cubegame.domain.events;

import org.cubegame.domain.exceptions.EnumException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Command {

    START("/start"),
    STOP("/stop");

    private final String value;

    private final static Map<String, Command> enumsByValue = Stream.of(values())
            .collect(Collectors.toMap(
                    command -> command.getValue().toUpperCase(),
                    command -> command)
            );

    static {
        for (Command item : values()) {
            enumsByValue.put(item.value.toUpperCase(), item);
        }
    }

    Command(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static Command from(final String value) {
        final Command createdValue = enumsByValue.get(value.toUpperCase());
        if (createdValue == null) {
            final List<String> allowedValues = Arrays.stream(values())
                    .map(Command::getValue)
                    .collect(Collectors.toList());

            throw new EnumException(Command.class.getName(), value, allowedValues);
        }
        return createdValue;
    }

}


