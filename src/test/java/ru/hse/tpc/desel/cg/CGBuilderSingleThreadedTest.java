package ru.hse.tpc.desel.cg;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;
import ru.hse.tpc.desel.domain.Marking;
import ru.hse.tpc.desel.domain.Transition;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class CGBuilderSingleThreadedTest {

    @Test
    public void build1Test() {
        Transition t1 = new Transition("t1",
                Collections.singletonList(ImmutablePair.of(0, 1)),
                Collections.singletonList(ImmutablePair.of(1, 1)));
        Transition t2 = new Transition("t2",
                Collections.singletonList(ImmutablePair.of(1, 1)),
                Collections.singletonList(ImmutablePair.of(0, 1)));
        Marking initialMarking = new Marking(1,0);
        CGBuilderSingleThreaded cgBuilder = new CGBuilderSingleThreaded(Arrays.asList(t1, t2));
        Map<Marking, Set<ImmutablePair<Transition, Marking>>> cg = cgBuilder.build(initialMarking);
        cg.forEach((key, value) -> {
            if (key.equals(initialMarking)) {
                System.out.print("ROOT ");
            }
            System.out.print(key + " -> ");
            value.forEach(System.out::print);
            System.out.println();
        });
    }

    @Test
    public void build2Test() {
        Transition t1 = new Transition("t1",
                Collections.singletonList(ImmutablePair.of(0, 1)),
                Arrays.asList(
                        ImmutablePair.of(1, 1),
                        ImmutablePair.of(2, 1)
                ));
        Transition t2 = new Transition("t2",
                Collections.singletonList(ImmutablePair.of(1, 1)),
                Collections.singletonList(ImmutablePair.of(0, 1)));
        Marking initialMarking = new Marking(1,0,0);
        CGBuilderSingleThreaded cgBuilder = new CGBuilderSingleThreaded(Arrays.asList(t1, t2));
        Map<Marking, Set<ImmutablePair<Transition, Marking>>> cg = cgBuilder.build(initialMarking);
        cg.forEach((key, value) -> {
            if (key.equals(initialMarking)) {
                System.out.print("ROOT ");
            }
            System.out.print(key + " -> ");
            value.forEach(System.out::print);
            System.out.println();
        });
    }

    @Test
    public void build3Test() {
        Transition t1 = new Transition("t1",
                Collections.singletonList(ImmutablePair.of(0,1)),
                Arrays.asList(
                        ImmutablePair.of(1,2),
                        ImmutablePair.of(2,1)
                ));
        Transition t2 = new Transition("t2",
                Arrays.asList(
                        ImmutablePair.of(1,1),
                        ImmutablePair.of(2,1)
                ),
                Collections.singletonList(ImmutablePair.of(0,1)));
        Marking initialMarking = new Marking(1,0,0);
        CGBuilderSingleThreaded cgBuilder = new CGBuilderSingleThreaded(Arrays.asList(t1,t2));
        Map<Marking, Set<ImmutablePair<Transition, Marking>>> cg = cgBuilder.build(initialMarking);
        cg.forEach((key, value) -> {
            if (key.equals(initialMarking)) {
                System.out.print("ROOT ");
            }
            System.out.print(key + " -> ");
            value.forEach(System.out::print);
            System.out.println();
        });
        assertEquals(4, cg.size());
    }

    @Test
    public void build4Test() {
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
                        ImmutablePair.of(2, 1),
                        ImmutablePair.of(3, 1)
                ),
                Collections.singletonList(ImmutablePair.of(4, 1)));
        Transition d = new Transition("d",
                Collections.singletonList(ImmutablePair.of(4, 1)),
                Collections.singletonList(ImmutablePair.of(3, 1)));

        Marking initialMarking = new Marking(1,0,0,1,0);
        CGBuilderSingleThreaded cgBuilder = new CGBuilderSingleThreaded(Arrays.asList(a, b, c, d));
        Map<Marking, Set<ImmutablePair<Transition, Marking>>> cg = cgBuilder.build(initialMarking);

        cg.forEach((key, value) -> {
            if (key.equals(initialMarking)) {
                System.out.print("ROOT ");
            }
            System.out.print(key + " -> ");
            value.forEach(System.out::print);
            System.out.println();
        });

        Map<Marking, Integer> expectedMarkingToEdgeNum = new HashMap<>(9);
        expectedMarkingToEdgeNum.put(new Marking(1,0,0,1,0), 1);
        expectedMarkingToEdgeNum.put(new Marking(0,1,1,1,0), 2);
        expectedMarkingToEdgeNum.put(new Marking(1,0,-1,1,0), 2);
        expectedMarkingToEdgeNum.put(new Marking(0,1,-1,1,0), 2);
        expectedMarkingToEdgeNum.put(new Marking(0,1,0,1,0), 1);
        expectedMarkingToEdgeNum.put(new Marking(0,1,0,0,1), 2);
        expectedMarkingToEdgeNum.put(new Marking(1,0,-1,0,1), 2);
        expectedMarkingToEdgeNum.put(new Marking(0,1,-1,0,1), 2);
        expectedMarkingToEdgeNum.put(new Marking(1,0,0,0,1), 2);

        Map<Marking, Integer> actualMarkingToEdgeNum = cg.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));

        assertEquals(expectedMarkingToEdgeNum.size(), actualMarkingToEdgeNum.size());
        actualMarkingToEdgeNum.forEach((k, v) -> {
            assertTrue(expectedMarkingToEdgeNum.containsKey(k));
            assertEquals(expectedMarkingToEdgeNum.get(k), v);
        });
    }
}