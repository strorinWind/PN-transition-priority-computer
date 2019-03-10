package ru.hse.tpc.priorities.st;

import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;
import ru.hse.tpc.priorities.common.SpineTreeNode;

import java.util.List;

public class SpineTreeBuilderImpl implements SpineTreeBuilder {
    @Override
    public SpineTreeNode build(Marking initialMarking, List<CyclicRun> cyclicRuns) {
        SpineTreeNode root = new SpineTreeNode(initialMarking);
        for (CyclicRun cyclicRun : cyclicRuns) {
            SpineTreeNode prevNode = root;
            for (Transition t : cyclicRun) {
                if (!prevNode.containsOutgoingTransition(t)) {
                    Marking newM = t.fire(prevNode.getM());
                    SpineTreeNode newNode = new SpineTreeNode(newM, t, prevNode);
                    prevNode.addChildNode(t, newNode);
                    prevNode = newNode;
                } else {
                    prevNode = prevNode.getNextNode(t);
                }
            }
        }

        return root;
    }
}
