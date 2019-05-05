package ru.hse.tpc.desel.ecn;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;
import ru.hse.tpc.desel.cg.CGBuilder;
import ru.hse.tpc.desel.cg.CGBuilderSingleThreaded;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ECNTraverserParallelTest {

    private Map<Marking, List<ImmutablePair<Transition, Marking>>> cg;
    private ECNMarking initialMarking;
    private Set<Transition> transitionSet;

    @Test
    public void findCyclicRunsTest() {
        ECNTraverser ecnTraverser = new ECNTraverserParallel(ForkJoinPool.commonPool());
        List<CyclicRun> cyclicRuns = ecnTraverser.findCyclicRuns(cg, initialMarking, transitionSet);
        System.out.println("======================= Cyclic Runs =======================");
        cyclicRuns.forEach(System.out::println);
        System.out.println("===========================================================");
        Set<String> expectedCycles = new HashSet<>(Arrays.asList(
                "babacd",
                "babcad",
                "babcda",
                "b|abacbd",
                "b|abcabd",
                "ba|bacbad"
        ));
        assertEquals(expectedCycles.size(), cyclicRuns.size());
        cyclicRuns.forEach(cr -> assertTrue(expectedCycles.contains(cr.toString())));
    }

    @Before
    public void setup() {
        Transition a = new Transition("a",
                Collections.singletonList(ImmutablePair.of(1, 1)),
                Collections.singletonList(ImmutablePair.of(0, 1)));
        Transition b = new Transition("b",
                Collections.singletonList(ImmutablePair.of(0, 1)),
                Arrays.asList(
                        ImmutablePair.of(1, 1),
                        ImmutablePair.of(2, 1)
                ));
        Transition c = new Transition("c",
                Arrays.asList(
                        ImmutablePair.of(2, 2),
                        ImmutablePair.of(3, 1)
                ),
                Collections.singletonList(ImmutablePair.of(4, 1)));
        Transition d = new Transition("d",
                Collections.singletonList(ImmutablePair.of(4, 1)),
                Collections.singletonList(ImmutablePair.of(3, 1)));

        List<Transition> transitions = Arrays.asList(a,b,c,d);
        Marking initialMarking = new Marking(1,0,0,1,0);

        CGBuilder cgBuilder = new CGBuilderSingleThreaded();
        this.cg = cgBuilder.build(initialMarking, transitions);
        Set<Integer> unboundedPlaces = findUnboundedPlaces(cg.keySet());
        Map<Integer, Integer> additionalPlacesMarking = unboundedPlaces.stream()
                .collect(Collectors.toMap(Function.identity(), initialMarking::getMarking));
        this.initialMarking = new ECNMarking(initialMarking, additionalPlacesMarking);
        this.transitionSet = new HashSet<>(transitions);
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