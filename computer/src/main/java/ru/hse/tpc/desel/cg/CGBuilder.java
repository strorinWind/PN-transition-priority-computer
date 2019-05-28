package ru.hse.tpc.desel.cg;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;

import java.util.List;
import java.util.Map;

/**
 * Petri net covering graph builder
 */
public interface CGBuilder {

    /**
     * Build covering graph for a given Petri net represented by initial marking and list of transitions
     * @param initialMarking - initial marking m0 of a Petri net
     * @param transitions - list of transitions
     * @return covering graph represented by map where key is a vertex of the covering graph and value is a set
     *  of pairs of adjacent vertices with corresponding transitions
     */
    Map<Marking, List<ImmutablePair<Transition, Marking>>> build(Marking initialMarking, List<Transition> transitions);

}
