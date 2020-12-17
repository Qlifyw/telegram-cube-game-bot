package org.cubegame.domain.model.game.state;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Phase {
    CANCELED(-1),

    EMPTY(0),
    CHOOSE_GAME(1),
    NUMBER_OF_ROUNDS(2),
    NUMBER_OF_PLAYERS(3),
    AWAIT_PLAYERS(4),
    STARTED(5),
    COMPLETED(6);

    private final int order;

    private static final Map<Integer, Phase> enumsByOrder = Stream.of(values())
            .collect(Collectors.toMap(Phase::getOrder, phase -> phase));

    private static final Map<String, Phase> enumsByValue = Stream.of(values())
            .collect(Collectors.toMap(
                    Enum::name,
                    phase -> phase)
            );

    Phase(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }

    public static Phase getNextFor(Phase phase) {
        return enumsByOrder.get(phase.getOrder() + 1);
    }

    public static Phase fromString(String value) {
        return enumsByValue.get(value);
    }

    @Override
    public String toString() {
        return this.name();
    }

}
