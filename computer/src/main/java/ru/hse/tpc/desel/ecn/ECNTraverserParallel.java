package ru.hse.tpc.desel.ecn;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Phaser;

public class ECNTraverserParallel extends ECNTraverserImpl {

    private static final int PHASER_THRESHOLD = 10_000;

    private final ExecutorService executorService;

    public ECNTraverserParallel(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public List<CyclicRun> findCyclicRuns(Map<Marking, List<ImmutablePair<Transition, Marking>>> cg,
                                          ECNMarking initialMarking, Set<Transition> transitionSet) {
        Phaser phaser = new Phaser(1);
        ConcurrentLinkedQueue<CyclicRun> q = new ConcurrentLinkedQueue<>();
        executorService.submit(getTask(new TraverseNode(initialMarking), transitionSet, q, cg, phaser));
        phaser.arriveAndAwaitAdvance();
        return new ArrayList<>(q);
    }

    private Runnable getTask(TraverseNode node, Set<Transition> transitionSet, ConcurrentLinkedQueue<CyclicRun> q,
                             Map<Marking, List<ImmutablePair<Transition, Marking>>> graph, Phaser phaser) {
        Phaser newPhaser = (phaser.getRegisteredParties() >= PHASER_THRESHOLD) ? new Phaser(phaser) : phaser;
        newPhaser.register();
        return () -> {
            try {
                Optional<CyclicRun> cyclicRunO = checkForCyclicRun(node);
                if (cyclicRunO.isPresent()) {
                    if (cyclicRunContainsAllTransitions(transitionSet, cyclicRunO.get())) {
                        q.add(cyclicRunO.get());
                    }
                }
                List<ImmutablePair<Transition, Marking>> validTransitions =
                        filterOutOccurredTransitions(node, graph.get(node.getMarking().getOriginalPlace()));
                System.out.println(executorService.toString() + "\n" +
                        "Path size: " + node.getPathSize() + " Total edges: " + graph.get(node.getMarking().getOriginalPlace()).size() +
                        " Valid: " + validTransitions.size());
                validTransitions.stream()
                        .map(toNewTraverseNode(node))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(n -> getTask(n, transitionSet, q, graph, newPhaser))
                        .forEach(executorService::submit);
                newPhaser.arriveAndDeregister();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        };
    }
}
