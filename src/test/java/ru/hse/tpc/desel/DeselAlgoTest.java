package ru.hse.tpc.desel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;
import ru.hse.tpc.desel.domain.CyclicRun;
import ru.hse.tpc.desel.domain.Marking;
import ru.hse.tpc.desel.domain.Transition;
import ru.hse.tpc.desel.ecn.ECNMarking;

import java.util.*;

import static org.junit.Assert.*;

public class DeselAlgoTest {


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
        List<CyclicRun> cyclicRuns = DeselAlgo.findCyclicRuns(Arrays.asList(a, b, c, d), initialMarking);
        System.out.println("======================= Cyclic Runs =======================");
        cyclicRuns.forEach(System.out::println);
        System.out.println("===========================================================");
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