package ru.hse.tpc.desel.ecn;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class ECNTraverserParallel extends ECNTraverserImpl {

    private final ForkJoinPool fjPool;

    public ECNTraverserParallel(ForkJoinPool fjPool) {
        this.fjPool = fjPool;
    }

    @Override
    public List<CyclicRun> findCyclicRuns(Map<Marking, List<ImmutablePair<Transition, Marking>>> cg,
                                          ECNMarking initialMarking, Set<Transition> transitionSet) {
        return fjPool.invoke(new ForkTraverse(new TraverseNode(initialMarking), transitionSet, cg));
    }

    private class ForkTraverse extends RecursiveTask<List<CyclicRun>> {
        private final TraverseNode node;
        private final Set<Transition> transitionSet;
        private final Map<Marking, List<ImmutablePair<Transition, Marking>>> graph;

        ForkTraverse(TraverseNode node, Set<Transition> transitionSet,
                     Map<Marking, List<ImmutablePair<Transition, Marking>>> graph) {
            this.node = node;
            this.transitionSet = transitionSet;
            this.graph = graph;
        }

        @Override
        protected List<CyclicRun> compute() {
            Optional<CyclicRun> cyclicRunO = checkForCyclicRun(node);
            if (cyclicRunO.isPresent()) {
                if (cyclicRunContainsAllTransitions(transitionSet, cyclicRunO.get())) {
                    return Collections.singletonList(cyclicRunO.get());
                }
            }
            List<ImmutablePair<Transition, Marking>> validTransitions =
                    filterOutOccurredTransitions(node, graph.get(node.getMarking().getOriginalPlace()));
            List<ForkTraverse> newTasks = validTransitions.stream()
                    .map(toNewTraverseNode(node))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(n -> new ForkTraverse(n, transitionSet, graph))
                    .collect(Collectors.toList());
            return invokeAll(newTasks).stream().map(ForkJoinTask::join).flatMap(Collection::stream).collect(Collectors.toList());
        }
    }
}
