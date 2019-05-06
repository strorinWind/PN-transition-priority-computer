package ru.hse.tpc.desel.ecn;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Traverser for walking through Extended Covering Net and searching for cyclic runs
 */
public interface ECNTraverser {

    /**
     * Finds all existing cyclic runs in a Petri net given covering graph, initial marking of Extended Covering Net
     * and transition set
     * @param cg - covering graph of an original Petri net
     * @param initialMarking - ECN initial marking
     * @param transitionSet - transition set
     * @return list of cyclic runs
     */
    List<CyclicRun> findCyclicRuns(Map<Marking, List<ImmutablePair<Transition, Marking>>> cg, ECNMarking initialMarking,
                                   Set<Transition> transitionSet);

}
