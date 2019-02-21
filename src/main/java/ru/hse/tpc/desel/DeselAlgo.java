package ru.hse.tpc.desel;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.desel.cg.CGBuilder;
import ru.hse.tpc.desel.cg.CGBuilderSingleThreaded;
import ru.hse.tpc.desel.domain.CyclicRun;
import ru.hse.tpc.desel.domain.Marking;
import ru.hse.tpc.desel.domain.Transition;
import ru.hse.tpc.desel.ecn.BacktrackingECNTraverser;
import ru.hse.tpc.desel.ecn.ECNMarking;
import ru.hse.tpc.desel.ecn.ECNTraverser;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DeselAlgo {

    public static List<CyclicRun> findCyclicRuns(List<Transition> transitions, Marking initialMarking) {
        CGBuilder cgBuilder = new CGBuilderSingleThreaded(transitions);
        Map<Marking, Set<ImmutablePair<Transition, Marking>>> cg = cgBuilder.build(initialMarking);
        System.out.println("======================= Covering Graph =======================");
        cg.forEach((key, value) -> {
            if (key.equals(initialMarking)) {
                System.out.print("ROOT ");
            }
            System.out.print(key + " -> ");
            value.forEach(System.out::print);
            System.out.println();
        });
        System.out.println("==============================================================");
        Set<Integer> unboundedPlaces = findUnboundedPlaces(cg.keySet());
        System.out.println("======================= Unbounded Places =======================");
        System.out.println(unboundedPlaces.stream().map(Object::toString).collect(Collectors.joining(", ")));
        System.out.println("================================================================");
        Map<Integer, Integer> additionalPlacesMarking = unboundedPlaces.stream()
                .collect(Collectors.toMap(Function.identity(), initialMarking::getMarking));
        System.out.println("======================= Additional Places Marking =======================");
        additionalPlacesMarking.forEach((key, value) -> System.out.println(key + " -> " + value));
        System.out.println("=========================================================================");
        ECNTraverser ecnTraverser = new BacktrackingECNTraverser();
        return ecnTraverser.findCyclicRuns(cg, new ECNMarking(initialMarking, additionalPlacesMarking));
    }

    private static Set<Integer> findUnboundedPlaces(Set<Marking> markings) {
        Set<Integer> result = new HashSet<>();
        for (Marking m : markings) {
            int curPlace = 0;
            for (int pm : m) {
                if (pm == Marking.OMEGA) {
                    result.add(curPlace);
                }
                curPlace++;
            }
        }
        return result;
    }

}
