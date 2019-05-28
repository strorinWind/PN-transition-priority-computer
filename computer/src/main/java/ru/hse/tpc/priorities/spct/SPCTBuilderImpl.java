package ru.hse.tpc.priorities.spct;

import com.google.common.flogger.FluentLogger;
import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;
import ru.hse.tpc.priorities.common.NodeColor;
import ru.hse.tpc.priorities.common.SpineTreeNode;

import java.util.*;
import java.util.stream.Collectors;

public class SPCTBuilderImpl implements SPCTBuilder {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    @Override
    public SpineTreeNode build(SpineTreeNode root, List<Transition> transitions) {
        Set<Marking> markingsInTree = new HashSet<>();
        Deque<SpineTreeNode> nodesToColor = new LinkedList<>();
        // Preprocess the spine tree coloring leaves in green, adding other nodes to color queue and adding all
        // markings in the tree to the set
        Deque<SpineTreeNode> preProcessingQ = new LinkedList<>();
        preProcessingQ.addFirst(root);
        int treeSize = 1;
        while (!preProcessingQ.isEmpty()) {
            SpineTreeNode node = preProcessingQ.removeFirst();
            if (node.isLeaf()) {
                node.color(NodeColor.GREEN);
                treeSize++;
            } else {
                nodesToColor.push(node);
                node.getChildNodes().forEach(preProcessingQ::addLast);
            }
            markingsInTree.add(node.getM());
        }
        // Process not colored nodes according to the algorithm
        while (!nodesToColor.isEmpty()) {
            SpineTreeNode node = nodesToColor.pop();
            treeSize++;
            List<SpineTreeNode> newNodes = transitions.stream()
                    .filter(t -> !node.containsOutgoingTransition(t))
                    .filter(t -> t.canOccur(node.getM()))
                    .map(t -> ImmutablePair.of(t, t.fire(node.getM())))
                    .filter(p -> markingsInTree.add(p.right))
                    .map(p -> new SpineTreeNode(p.right, p.left, node))
                    .collect(Collectors.toList());
            for (SpineTreeNode newNode : newNodes) {
                node.addChildNode(newNode.getIncT(), newNode);
                colorNewNode(newNode);
                if (newNode.getColor() == NodeColor.NOT_COLORED) {
                    nodesToColor.push(newNode);
                }
            }
            node.color(NodeColor.YELLOW);
        }

        logger.atInfo().log("Spined-based coverability tree size: %d nodes", treeSize);
        return root;
    }

    private void colorNewNode(SpineTreeNode newNode) {
        boolean sameMarkingOnPathExists = false;
        SpineTreeNode curNode = newNode.getParent();
        while (curNode != null) {
            if (curNode.getM().isStrictlyCoveredBy(newNode.getM())) {
                newNode.color(NodeColor.RED);
                return;
            } else if (curNode.getM().equals(newNode.getM())) {
                sameMarkingOnPathExists = true;
            }
            curNode = curNode.getParent();
        }
        if (sameMarkingOnPathExists) {
            newNode.color(NodeColor.GREEN);
        }
    }

}
