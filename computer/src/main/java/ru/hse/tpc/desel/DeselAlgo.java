package ru.hse.tpc.desel;

import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;

import java.util.List;

public interface DeselAlgo {

    /**
     * Find all feasible cycles
     * @param transitions - list of transitions
     * @param initialMarking - initial marking
     * @return list of all feasible cycles existing in the net
     */
    List<CyclicRun> findCyclicRuns(List<Transition> transitions, Marking initialMarking);

    /**
     * Find limited number of feasible cycles
     * @param transitions - list of transitions
     * @param initialMarking - initial marking
     * @param limit - number of found feasible cycles at which the search should stop
     * @return list of feasible cycles, number of which does not exceed limit
     */
    List<CyclicRun> findCyclicRuns(List<Transition> transitions, Marking initialMarking, int limit);
}
