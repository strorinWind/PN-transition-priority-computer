package ru.hse.tpc.desel.cg;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;

import java.util.*;

public class CGBuilderSingleThreaded extends AbstractCGBuilder {

    @Override
    public Map<Marking, List<ImmutablePair<Transition, Marking>>> build(Marking initialMarking, List<Transition> transitions) {
        Map<Marking, List<ImmutablePair<Transition, Marking>>> graph = new HashMap<>();
        Deque<CGVertex> q = new LinkedList<>();
        q.addFirst(new CGVertex(initialMarking, null));
        while (!q.isEmpty()) {
            CGVertex v = q.remove();
            Marking vm = v.getM();
            if (graph.putIfAbsent(vm, new ArrayList<>()) == null) {
                transitions.stream()
                        .filter(t -> t.canOccur(vm))
                        .map(t -> ImmutablePair.of(t, generalize(t.fire(vm), v)))
                        .peek(p -> graph.get(vm).add(p))
                        .forEach(p -> q.addLast(new CGVertex(p.right, v)));
            }
        }
        return graph;
    }
}
