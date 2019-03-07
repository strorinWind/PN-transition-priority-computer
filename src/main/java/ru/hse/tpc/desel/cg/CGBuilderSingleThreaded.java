package ru.hse.tpc.desel.cg;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.desel.domain.Marking;
import ru.hse.tpc.desel.domain.Transition;

import java.util.*;
import java.util.stream.Collectors;

public class CGBuilderSingleThreaded extends AbstractCGBuilder {

    private final List<Transition> transitions;

    public CGBuilderSingleThreaded(List<Transition> transitions) {
        this.transitions = transitions;
    }

    @Override
    public Map<Marking, Set<ImmutablePair<Transition, Marking>>> build(Marking initialMarking) {
        Map<Marking, Set<ImmutablePair<Transition, Marking>>> graph = new HashMap<>();
        graph.put(initialMarking, new HashSet<>());
        CGVertex root = new CGVertex(initialMarking, null);
        Queue<ImmutablePair<CGVertex, Transition>> workQ = transitions.stream().filter(t -> t.canOccur(initialMarking))
                .map(t -> ImmutablePair.of(root, t)).collect(Collectors.toCollection(LinkedList::new));
        while (!workQ.isEmpty()) {
            ImmutablePair<CGVertex, Transition> work = workQ.remove();
            CGVertex v = work.left;
            Transition t = work.right;
            Marking newMarking = t.fire(v.getM());
            Marking generalizedMarking = generalize(newMarking, v);
            graph.get(v.getM()).add(ImmutablePair.of(t, generalizedMarking));
            if (graph.putIfAbsent(generalizedMarking, new HashSet<>()) == null) { // check if the marking occurred for the 1st time
                CGVertex newVertex = new CGVertex(generalizedMarking, v);
                workQ.addAll(
                        transitions.stream().filter(tr -> tr.canOccur(generalizedMarking))
                                .map(tr -> ImmutablePair.of(newVertex, tr)).collect(Collectors.toList())
                );
            }
        }
        return graph;
    }
}