package ru.hse.tpc.desel;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;
import ru.hse.tpc.desel.cg.CGBuilder;
import ru.hse.tpc.desel.cg.CGBuilderParallel;
import ru.hse.tpc.desel.ecn.ECNMarking;
import ru.hse.tpc.desel.ecn.ECNTraverser;
import ru.hse.tpc.desel.ecn.ECNTraverserParallel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DeselAlgoParallel extends AbstractDeselAlgo {

    private final ForkJoinPool forkJoinPool;

    public DeselAlgoParallel(ForkJoinPool forkJoinPool) {
        this.forkJoinPool = forkJoinPool;
    }

    @Override
    public List<CyclicRun> findCyclicRuns(List<Transition> transitions, Marking initialMarking) {
        CGBuilder cgBuilder = new CGBuilderParallel(forkJoinPool);
        Map<Marking, List<ImmutablePair<Transition, Marking>>> cg = cgBuilder.build(initialMarking, transitions);

        System.out.println("CG built. Size: " + cg.size());

        Set<Integer> unboundedPlaces = findUnboundedPlaces(cg.keySet());
        Map<Integer, Integer> additionalPlacesMarking = unboundedPlaces.stream()
                .collect(Collectors.toMap(Function.identity(), initialMarking::getMarking));
        ECNTraverser ecnTraverser = new ECNTraverserParallel(forkJoinPool);
        return ecnTraverser.findCyclicRuns(cg, new ECNMarking(initialMarking, additionalPlacesMarking),
                new HashSet<>(transitions));
    }
}
