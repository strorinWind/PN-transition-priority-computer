package ru.hse.tpc.priorities.pr;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.common.Transition;
import ru.hse.tpc.priorities.common.SpineTreeNode;

import java.util.Set;

/**
 * Priority relation computer
 */
public interface PriorityRelationComputer {

    /**
     * Computes the set of transitions priority relations given a spine based covering tree
     * @param spct - spine base covering tree
     * @return the set of priority relations
     */
    Set<ImmutablePair<Transition, Transition>> compute(SpineTreeNode spct);

}
