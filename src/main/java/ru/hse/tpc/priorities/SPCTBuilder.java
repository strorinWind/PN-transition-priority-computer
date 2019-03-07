package ru.hse.tpc.priorities;

import ru.hse.tpc.desel.domain.Transition;
import ru.hse.tpc.priorities.domain.SpineTreeNode;

import java.util.List;

/**
 * Spine-based covering tree constructor
 */
public interface SPCTBuilder {

    SpineTreeNode build(SpineTreeNode spineTree, List<Transition> transitions);

}
