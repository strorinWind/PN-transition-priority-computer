package ru.hse.tpc.desel.cg;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CGBuilderParallel extends AbstractCGBuilder {

    private final ForkJoinPool fjPool;

    public CGBuilderParallel(ForkJoinPool fjPool) {
        this.fjPool = fjPool;
    }

    @Override
    public Map<Marking, List<ImmutablePair<Transition, Marking>>> build(Marking initialMarking, List<Transition> transitions) {
        // <DEBUG>
        //System.out.println(CGBuilderParallel.class.getSimpleName() +" FJPool: " + fjPool.toString());
        // </DEBUG>
        ConcurrentMap<Marking, List<ImmutablePair<Transition, Marking>>> graph = new ConcurrentHashMap<>();
        fjPool.invoke(new ForkBuild(new CGVertex(initialMarking, null), transitions, graph));
        // <DEBUG>
        //System.out.println(CGBuilderParallel.class.getSimpleName() +" FJPool: " + fjPool.toString());
        // </DEBUG>
        return graph;
    }

    private class ForkBuild extends RecursiveAction {
        private final CGVertex v;
        private final List<Transition> transitions;
        private final ConcurrentMap<Marking, List<ImmutablePair<Transition, Marking>>> graph;

        ForkBuild(CGVertex v, List<Transition> transitions,
                  ConcurrentMap<Marking, List<ImmutablePair<Transition, Marking>>> graph) {
            this.v = v;
            this.transitions = transitions;
            this.graph = graph;
        }

        @Override
        protected void compute() {
            Marking vm = v.getM();
            // <DEBUG>
            //System.out.println("Executing computations for marking " + vm + " in thread " + Thread.currentThread().getId());
            // </DEBUG>
            if (graph.putIfAbsent(vm, new ArrayList<>()) == null) {
                List<ForkBuild> tasks = transitions.stream()
                        .filter(t -> t.canOccur(vm))
                        .map(t -> ImmutablePair.of(t, generalize(t.fire(vm), v)))
                        .peek(p -> graph.get(vm).add(p))
                        .map(p -> new ForkBuild(new CGVertex(p.right, v), transitions, graph))
                        .collect(Collectors.toList());
                invokeAll(tasks);
            }
        }
    }

}
