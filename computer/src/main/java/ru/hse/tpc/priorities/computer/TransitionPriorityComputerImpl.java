package ru.hse.tpc.priorities.computer;

import com.google.common.flogger.FluentLogger;
import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.common.Transition;

import java.util.*;
import java.util.stream.Collectors;

public class TransitionPriorityComputerImpl implements TransitionPriorityComputer {

    private final static FluentLogger logger = FluentLogger.forEnclosingClass();

    private enum  Mark {
        TEMP,
        PERM
    }

    @Override
    public Map<Transition, Integer> compute(Set<ImmutablePair<Transition, Transition>> priorityRelation,
                                            List<Transition> transitions) {
        Map<Transition, Set<Transition>> graph = new HashMap<>();
        for (ImmutablePair<Transition, Transition> relation : priorityRelation) {
            graph.computeIfAbsent(relation.left, t -> new HashSet<>()).add(relation.right);
        }
        Collection<Transition> sorted = sort(graph);
        logger.atInfo().log("Transitions sorted by priority relation: %s",
                sorted.stream().map(Transition::toString).collect(Collectors.joining(" << ")));
        Map<Transition, Integer> result = new HashMap<>(transitions.size());
        int priorityVal = 1;
        for (Transition t : sorted) {
            result.put(t, priorityVal++);
        }
        for (Transition t : transitions) {
            result.putIfAbsent(t, 1);
        }
        return result;
    }

    private Collection<Transition> sort(Map<Transition, Set<Transition>> graph) {
        Deque<Transition> sorted = new LinkedList<>();
        Map<Transition, Mark> visitedNodes = new HashMap<>(graph.size());
        for (Transition node : graph.keySet()) {
            if (!visitedNodes.containsKey(node)) {
                visit(node, graph, sorted, visitedNodes);
            }
        }
        return sorted;
    }

    private void visit(Transition node, Map<Transition, Set<Transition>> graph, Deque<Transition> sorted,
                       Map<Transition, Mark> visitedNodes) {
        if (visitedNodes.get(node) == Mark.PERM) {
            return;
        }
        if (visitedNodes.get(node) == Mark.TEMP) {
            throw new TransitionPriorityComputationException("A cycle occurred in priority relation graph." +
                    " Priority solution does not exist.");
        }
        visitedNodes.put(node, Mark.TEMP);
        for (Transition childNode : graph.getOrDefault(node, Collections.emptySet())) {
            visit(childNode, graph, sorted, visitedNodes);
        }
        visitedNodes.put(node, Mark.PERM);
        sorted.addFirst(node);
    }
}
