package ru.hse.tpc.desel.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CyclicRun {

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
}
