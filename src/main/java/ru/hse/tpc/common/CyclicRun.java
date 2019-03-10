package ru.hse.tpc.common;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a single cyclic run in a Petri net
 */
public class CyclicRun implements Iterable<Transition> {

    private final List<Transition> prefix;
    private final List<Transition> cycle;

    public CyclicRun(Collection<Transition> prefix, Collection<Transition> cycle) {
        this.prefix = new ArrayList<>(prefix);
        this.cycle = new ArrayList<>(cycle);
    }

    public List<Transition> getPrefix() {
        return Collections.unmodifiableList(prefix);
    }

    public List<Transition> getCycle() {
        return Collections.unmodifiableList(cycle);
    }

    @Override
    public String toString() {
        String prefixStr = prefix.stream().map(Transition::toString).collect(Collectors.joining("", "", "|"));
        return ((prefixStr.length() == 1) ? "" : prefixStr) +
                cycle.stream().map(Transition::toString).collect(Collectors.joining(""));
    }

    @Override
    public Iterator<Transition> iterator() {
        return new Iterator<Transition>() {

            private Iterator<Transition> prefixIter = prefix.iterator();
            private Iterator<Transition> cycleIter = cycle.iterator();

            @Override
            public boolean hasNext() {
                return prefixIter.hasNext() || cycleIter.hasNext();
            }

            @Override
            public Transition next() {
                if (prefixIter.hasNext()) {
                    return prefixIter.next();
                } else {
                    return cycleIter.next();
                }
            }
        };
    }
}
