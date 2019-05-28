package ru.hse.tpc.priorities.spct;

import ru.hse.tpc.common.Transition;
import ru.hse.tpc.priorities.common.SpineTreeNode;

import java.util.List;

/**
 * Spine-based covering tree constructor
 */
public interface SPCTBuilder {

    /**
     * Builds a spine-based covering tree by mutating the given spine tree: adds new nodes and colors all nodes
     * @param spineTree - spine tree
     * @param transitions - list of transition of the original Petri net
     * @return the root of spine-based covering tree
     */
    SpineTreeNode build(SpineTreeNode spineTree, List<Transition> transitions);

}
