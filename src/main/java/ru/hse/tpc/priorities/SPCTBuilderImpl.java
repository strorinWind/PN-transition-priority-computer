package ru.hse.tpc.priorities;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.desel.domain.Marking;
import ru.hse.tpc.desel.domain.Transition;
import ru.hse.tpc.priorities.domain.NodeColor;
import ru.hse.tpc.priorities.domain.SpineTreeNode;

import java.util.*;
import java.util.stream.Collectors;

public class SPCTBuilderImpl implements SPCTBuilder {

    @Override
    public SpineTreeNode build(SpineTreeNode root, List<Transition> transitions) {
        Set<Marking> markingsInTree = new HashSet<>();
        Deque<SpineTreeNode> nodesToColor = new LinkedList<>();
        // Preprocess the spine tree coloring leaves in green, adding other nodes to color queue and adding all
        // markings in the tree to the set
        Deque<SpineTreeNode> preProcessingQ = new LinkedList<>();
        preProcessingQ.addFirst(root);
        while (!preProcessingQ.isEmpty()) {
            SpineTreeNode node = preProcessingQ.removeFirst();
            if (node.isLeaf()) {
                node.color(NodeColor.GREEN);
            } else {
                nodesToColor.push(node);
                node.getChildNodes().forEach(preProcessingQ::addLast);
            }
            markingsInTree.add(node.getM());
        }
        // Process not colored nodes according to the algorithm
        while (!nodesToColor.isEmpty()) {
            SpineTreeNode node = nodesToColor.pop();
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
