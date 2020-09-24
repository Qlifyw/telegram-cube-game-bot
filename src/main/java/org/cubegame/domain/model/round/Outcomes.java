package org.cubegame.domain.model.round;

import org.cubegame.domain.model.identifier.UserId;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Outcomes implements Iterable {

    private final Map<UserId, Outcome> results = new LinkedHashMap<>();

    public boolean contains(UserId userId) {
        return results.get(userId) != null;
    }

    public void add(Outcome outcome) {
        results.putIfAbsent(outcome.getPlayer().getUserId(), outcome);
    }

    public Outcome get(UserId userId) {
        return results.get(userId);
    }

    public int size() {
        return results.size();
    }

    public Stream<Outcome> stream() {
        return results.values().stream();
    }

    @Override
    public Iterator iterator() {
        return results.values().iterator();
    }

    @Override
    public void forEach(final Consumer consumer) {
        results.values().forEach(consumer);
    }

    @Override
    public Spliterator spliterator() {
        return results.values().spliterator();
    }

}
