package ru.hse.tpc.desel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;
import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;
import ru.hse.tpc.desel.ecn.ECNMarking;

import java.util.*;

import static org.junit.Assert.*;

public class DeselAlgoSingleThreadedTest {


    @Test
    public void findCyclicRunsTest() {
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

        Marking initialMarking = new Marking(1,0,0,1,0);
        List<CyclicRun> cyclicRuns = new DeselAlgoSingleThreaded().findCyclicRuns(Arrays.asList(a, b, c, d), initialMarking);
        System.out.println("======================= Cyclic Runs =======================");
        cyclicRuns.forEach(System.out::println);
        System.out.println("===========================================================");
        Set<String> expectedCycles = new HashSet<>(Arrays.asList(
                "b -> a -> b -> a -> c -> d",
                "b -> a -> b -> c -> a -> d",
                "b -> a -> b -> c -> d -> a",
                "b | a -> b -> a -> c -> b -> d",
                "b | a -> b -> c -> a -> b -> d",
                "b -> a | b -> a -> c -> b -> a -> d"
        ));
        assertEquals(expectedCycles.size(), cyclicRuns.size());
        cyclicRuns.forEach(cr -> assertTrue(expectedCycles.contains(cr.toString())));
    }

    @Test
    public void test() {
        Map<Integer, Integer> am1 = new HashMap<>(1);
        am1.put(2, 0);
        ECNMarking m1 = new ECNMarking(new Marking(1,0,0,1,0), am1);
        Map<Integer, Integer> am2 = new HashMap<>(1);
        am2.put(2, 0);
        ECNMarking m2 = new ECNMarking(new Marking(1,0,-1,1,0), am2);
        assertEquals(m1, m2);
    }
}