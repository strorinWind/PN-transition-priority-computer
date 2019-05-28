package ru.hse.tpc.priorities.st;

import com.google.common.flogger.FluentLogger;
import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;
import ru.hse.tpc.priorities.common.SpineTreeNode;

import java.util.List;

public class SpineTreeBuilderImpl implements SpineTreeBuilder {

    private final static FluentLogger logger = FluentLogger.forEnclosingClass();

    @Override
    public SpineTreeNode build(Marking initialMarking, List<CyclicRun> cyclicRuns) {
        SpineTreeNode root = new SpineTreeNode(initialMarking);
        int treeSize = 1;
        for (CyclicRun cyclicRun : cyclicRuns) {
            SpineTreeNode prevNode = root;
            for (Transition t : cyclicRun) {
                if (!prevNode.containsOutgoingTransition(t)) {
                    Marking newM = t.fire(prevNode.getM());
                    SpineTreeNode newNode = new SpineTreeNode(newM, t, prevNode);
                    treeSize++;
                    prevNode.addChildNode(t, newNode);
                    prevNode = newNode;
                } else {
                    prevNode = prevNode.getNextNode(t);
                }
            }
        }
        logger.atInfo().log("Spine tree size: %d nodes", treeSize);
        return root;
    }
}
