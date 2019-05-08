package ru.hse.tpc.priorities.pr;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import ru.hse.tpc.desel.DeselAlgoSingleThreaded;
import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;
import ru.hse.tpc.priorities.common.SpineTreeNode;
import ru.hse.tpc.priorities.spct.SPCTBuilderImpl;
import ru.hse.tpc.priorities.st.SpineTreeBuilderImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PriorityRelationComputerImplTest {

    private SpineTreeNode spct;

    @Test
    public void computeTest() {
        Set<ImmutablePair<Transition, Transition>> priorityRelation = new PriorityRelationComputerImpl().compute(spct);
        System.out.println("================= Priority Relation =================");
        priorityRelation.forEach(e -> System.out.println(e.left + " << " + e.right));
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

        Marking initialMarking = new Marking(1,0,0,1,0);
        List<Transition> transitions = Arrays.asList(a, b, c, d);
        List<CyclicRun> cyclicRuns = new DeselAlgoSingleThreaded().findCyclicRuns(transitions, initialMarking);
        SpineTreeNode spineTree = new SpineTreeBuilderImpl().build(initialMarking, cyclicRuns);
        this.spct = new SPCTBuilderImpl().build(spineTree, transitions);
    }
}