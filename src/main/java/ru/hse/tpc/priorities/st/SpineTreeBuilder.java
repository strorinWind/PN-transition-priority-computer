package ru.hse.tpc.priorities.st;

import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.priorities.common.SpineTreeNode;

import java.util.List;

/**
 * Spine tree builder
 */
public interface SpineTreeBuilder {

    /**
     * Builds spine tree
     * @param initialMarking - the initial marking of an original Petri net
     * @param cyclicRuns - list of cyclic runs
     * @return the root of the built spine tree
     */
    SpineTreeNode build(Marking initialMarking, List<CyclicRun> cyclicRuns);

}
