package ru.hse.tpc.priorities.pr;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.common.Transition;
import ru.hse.tpc.priorities.common.NodeColor;
import ru.hse.tpc.priorities.common.SpineTreeNode;

import java.util.*;
import java.util.stream.Collectors;

public class PriorityRelationComputerImpl implements PriorityRelationComputer {

    @Override
    public Set<ImmutablePair<Transition, Transition>> compute(SpineTreeNode spct) {
        Set<ImmutablePair<Transition, Transition>> priorityRelation = new HashSet<>();
        Deque<SpineTreeNode> q = new LinkedList<>();
        q.addFirst(spct);
        while (!q.isEmpty()) {
            SpineTreeNode node = q.removeFirst();
            Map<NodeColor, List<SpineTreeNode>> colorIndex = node.getChildNodes().stream()
                    .collect(Collectors.groupingBy(SpineTreeNode::getColor));
            colorIndex.getOrDefault(NodeColor.YELLOW, Collections.emptyList()).forEach(q::addLast);
            for (SpineTreeNode redNode : colorIndex.getOrDefault(NodeColor.RED, Collections.emptyList())) {
                for (SpineTreeNode yellowNode : colorIndex.getOrDefault(NodeColor.YELLOW, Collections.emptyList())) {
                    priorityRelation.add(ImmutablePair.of(yellowNode.getIncT(), redNode.getIncT()));
                }
                for (SpineTreeNode greenNode : colorIndex.getOrDefault(NodeColor.GREEN, Collections.emptyList())) {
                    priorityRelation.add(ImmutablePair.of(greenNode.getIncT(), redNode.getIncT()));
                }
            }
        }

        return priorityRelation;
    }

}
