package ru.hse.tpc.priorities.computer;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.common.Transition;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Computer of priority values for transitions
 */
public interface TransitionPriorityComputer {

    /**
     * Computes a mapping from transition to its priority value
     * @param priorityRelation - priority relation
     * @param transitions - list of transitions
     * @return mapping from transition to its priority value
     */
    Map<Transition, Integer> compute(Set<ImmutablePair<Transition, Transition>> priorityRelation,
                                     List<Transition> transitions);

}
