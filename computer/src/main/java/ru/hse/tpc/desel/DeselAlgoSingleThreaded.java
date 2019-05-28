package ru.hse.tpc.desel;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.desel.cg.CGBuilder;
import ru.hse.tpc.desel.cg.CGBuilderSingleThreaded;
import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;
import ru.hse.tpc.desel.ecn.ECNMarking;
import ru.hse.tpc.desel.ecn.ECNTraverser;
import ru.hse.tpc.desel.ecn.ECNTraverserImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An entry point for Desel algorithm of finding cyclic runs in a given Petri net
 */
public class DeselAlgoSingleThreaded extends AbstractDeselAlgo {

    public List<CyclicRun> findCyclicRuns(List<Transition> transitions, Marking initialMarking) {
        CGBuilder cgBuilder = new CGBuilderSingleThreaded();
        Map<Marking, List<ImmutablePair<Transition, Marking>>> cg = cgBuilder.build(initialMarking, transitions);
        // <DEBUG>
        System.out.println("Covering graph:");
        cg.forEach((key, value) -> {
            if (key.equals(initialMarking)) {
                System.out.print("ROOT ");
            }
            System.out.print(key + " -> ");
            value.forEach(System.out::print);
            System.out.println();
        });
        // </DEBUG>
        Set<Integer> unboundedPlaces = findUnboundedPlaces(cg.keySet());
        // <DEBUG>
        System.out.println("Unbounded places indices:");
        System.out.println(unboundedPlaces.stream().map(Object::toString).collect(Collectors.joining(", ")));
        // </DEBUG>
        Map<Integer, Integer> additionalPlacesMarking = unboundedPlaces.stream()
                .collect(Collectors.toMap(Function.identity(), initialMarking::getMarking));
        // <DEBUG>
        System.out.println("Initial markings for additional places:");
        additionalPlacesMarking.forEach((key, value) -> System.out.println("m(" + key + ")=" + value));
        // </DEBUG>
        ECNTraverser ecnTraverser = new ECNTraverserImpl();
        return ecnTraverser.findCyclicRuns(cg, new ECNMarking(initialMarking, additionalPlacesMarking),
                new HashSet<>(transitions));
    }

    @Override
    public List<CyclicRun> findCyclicRuns(List<Transition> transitions, Marking initialMarking, int limit) {
        throw new NotImplementedException("Not implemented");
    }

}
