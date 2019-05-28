package ru.hse.tpc.desel.cg;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class CGBuilderParallel extends AbstractCGBuilder {

    private static final int PHASER_THRESHOLD = 10_000;

    private final ExecutorService executorService;

    public CGBuilderParallel(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public Map<Marking, List<ImmutablePair<Transition, Marking>>> build(Marking initialMarking, List<Transition> transitions) {
        Phaser phaser = new Phaser(1);
        ConcurrentMap<Marking, List<ImmutablePair<Transition, Marking>>> graph = new ConcurrentHashMap<>();
        executorService.submit(getTask(new CGVertex(initialMarking, null), transitions, graph, phaser));
        phaser.arriveAndAwaitAdvance();
        return graph;
    }

    private Runnable getTask(CGVertex v, List<Transition> transitions,
                             ConcurrentMap<Marking, List<ImmutablePair<Transition, Marking>>> graph, Phaser phaser) {
        Phaser newPhaser = (phaser.getRegisteredParties() >= PHASER_THRESHOLD) ? new Phaser(phaser) : phaser;
        newPhaser.register();
        return () -> {
            try {
                Marking vm = v.getM();
                if (graph.putIfAbsent(vm, new ArrayList<>()) == null) {
                    transitions.stream()
                            .filter(t -> t.canOccur(vm))
                            .map(t -> ImmutablePair.of(t, generalize(t.fire(vm), v)))
                            .peek(p -> graph.get(vm).add(p))
                            .map(p -> getTask(new CGVertex(p.right, v), transitions, graph, newPhaser))
                            .forEach(executorService::submit);
                }
                newPhaser.arriveAndDeregister();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
