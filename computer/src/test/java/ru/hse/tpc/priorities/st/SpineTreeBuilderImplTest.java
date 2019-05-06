package ru.hse.tpc.priorities.st;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import ru.hse.tpc.desel.DeselAlgo;
import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;
import ru.hse.tpc.priorities.common.SpineTreeNode;
import ru.hse.tpc.priorities.st.SpineTreeBuilder;
import ru.hse.tpc.priorities.st.SpineTreeBuilderImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpineTreeBuilderImplTest {

    private Marking initialMarking;
    private List<CyclicRun> cyclicRuns;

    @Test
    public void buildTest() {
        SpineTreeBuilder stb = new SpineTreeBuilderImpl();
        SpineTreeNode tree = stb.build(initialMarking, cyclicRuns);
        System.out.println(tree);
    }

    @Before
    public void setUp() {
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

        this.initialMarking = new Marking(1,0,0,1,0);
        this.cyclicRuns = DeselAlgo.findCyclicRuns(Arrays.asList(a, b, c, d), this.initialMarking);
    }
}