package org.cubegame.domain.events;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Command {

    START("/start"),
    STOP("/stop");

    private final String value;

    public static final Map<String, Command> enumsByValue = Stream.of(values())
            .collect(Collectors.toMap(
                    command -> command.getValue().toUpperCase(),
                    command -> command)
            );

    static final List<String> allowedValues = Arrays.stream(values())
            .map(Command::getValue)
            .collect(Collectors.toList());

    Command(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static Optional<Command> from(final String value) {
        final Command createdValue = enumsByValue.get(value.toUpperCase());
        if (createdValue == null)
            return Optional.empty();

        return Optional.of(createdValue);
    }

}


