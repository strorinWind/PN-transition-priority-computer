package ru.hse.tpc.desel.cg;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class CGBuilderParallel extends AbstractCGBuilder {

    private final ExecutorService executorService;
    private Phaser phaser;

    public CGBuilderParallel(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public Map<Marking, List<ImmutablePair<Transition, Marking>>> build(Marking initialMarking, List<Transition> transitions) {
        phaser = new Phaser(1);
        ConcurrentMap<Marking, List<ImmutablePair<Transition, Marking>>> graph = new ConcurrentHashMap<>();
        executorService.submit(getTask(new CGVertex(initialMarking, null), transitions, graph));
        phaser.arriveAndAwaitAdvance();
        return graph;
    }

    private Runnable getTask(CGVertex v, List<Transition> transitions,
                             ConcurrentMap<Marking, List<ImmutablePair<Transition, Marking>>> graph) {
        phaser.register();
        return () -> {
            Marking vm = v.getM();
            if (graph.putIfAbsent(vm, new ArrayList<>()) == null) {
                transitions.stream()
                        .filter(t -> t.canOccur(vm))
                        .map(t -> ImmutablePair.of(t, generalize(t.fire(vm), v)))
                        .peek(p -> graph.get(vm).add(p))
                        .map(p -> getTask(new CGVertex(p.right, v), transitions, graph))
                        .forEach(executorService::submit);
            }
            phaser.arriveAndDeregister();
        };
    }
}
