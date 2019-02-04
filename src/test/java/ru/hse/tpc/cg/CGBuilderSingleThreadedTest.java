package ru.hse.tpc.cg;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;
import ru.hse.tpc.domain.Marking;
import ru.hse.tpc.domain.Transition;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class CGBuilderSingleThreadedTest {

    // (1)->|t1|->()
    //  | <-|t2|<- |
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
}