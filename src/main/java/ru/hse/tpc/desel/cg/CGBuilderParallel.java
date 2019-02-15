package ru.hse.tpc.desel.cg;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.desel.domain.Marking;
import ru.hse.tpc.desel.domain.Transition;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CGBuilderParallel extends AbstractCGBuilder {

    private final ForkJoinPool fjPool;

    private final List<Transition> transitions;
    private ConcurrentMap<Marking, Set<ImmutablePair<Transition, Marking>>> graph;

    public CGBuilderParallel(ForkJoinPool fjPool, List<Transition> transitions) {
        this.fjPool = fjPool;
        this.transitions = transitions;
    }

    public Map<Marking, Set<ImmutablePair<Transition, Marking>>> build(Marking initialMarking) {
        System.out.println("FJPool - " + fjPool.toString());
        this.graph = new ConcurrentHashMap<>();
        this.graph.put(initialMarking, new HashSet<>());
        CGVertex root = new CGVertex(initialMarking, null);
        List<Future<Boolean>> futures = fjPool.invokeAll(
                this.transitions.stream().filter(tr -> tr.canOccur(initialMarking))
                        .map(tr -> (Callable<Boolean>) () -> {
                            //System.out.println("FIRST LEVEL TASK: thread - " + Thread.currentThread().getId() + "; transition - " + tr);
                            new ForkBuild(ImmutablePair.of(root, tr)).compute();
                            return true;
                        }).collect(Collectors.toList())
        );
        for (Future<Boolean> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println("FJPool - " + fjPool.toString());
        return graph;
    }

    private class ForkBuild extends RecursiveAction {
        private final ImmutablePair<CGVertex, Transition> workUnit;

        ForkBuild(ImmutablePair<CGVertex, Transition> workUnit) {
            this.workUnit = workUnit;
        }

        @Override
        protected void compute() {
            System.out.println("Executing transition " + workUnit.right + " for marking " + workUnit.left.getM() + " by thread " + Thread.currentThread().getId());
            CGVertex v = workUnit.left;
            Transition t = workUnit.right;
            Marking newMarking = t.fire(v.getM());
            Marking generalizedMarking = CGBuilderParallel.this.generalize(newMarking, v);
            CGBuilderParallel.this.graph.get(v.getM()).add(ImmutablePair.of(t, generalizedMarking));
            // check if the marking occurred for the 1st time
            if (CGBuilderParallel.this.graph.putIfAbsent(generalizedMarking, new HashSet<>()) == null) {
                CGVertex newVertex = new CGVertex(generalizedMarking, v);
                invokeAll(
                        CGBuilderParallel.this.transitions.stream().filter(tr -> tr.canOccur(generalizedMarking))
                                .map(tr -> new ForkBuild(ImmutablePair.of(newVertex, tr))).collect(Collectors.toList())
                );
            }
        }
    }

}
