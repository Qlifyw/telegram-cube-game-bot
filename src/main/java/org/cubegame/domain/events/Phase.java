package org.cubegame.domain.events;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Phase {
    EMPTY(0),
    CHOOSE_GAME(1),
    NUMBER_OF_ROUNDS(2),
    NUMBER_OF_PLAYERS(3),
    AWAIT_PLAYERS(4),
    STARTED(5),
    COMPLETED(6);

    private final int order;

    private final static Map<Integer, Phase> enumsByOrder = Stream.of(values())
            .collect(Collectors.toMap(Phase::getOrder, phase -> phase));

    Phase(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }

    public static Phase getNextFor(Phase phase) {
        return enumsByOrder.get(phase.getOrder() + 1);
    }

}
