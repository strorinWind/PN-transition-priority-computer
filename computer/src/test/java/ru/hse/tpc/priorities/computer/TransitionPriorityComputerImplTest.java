package ru.hse.tpc.priorities.computer;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;
import ru.hse.tpc.common.Transition;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransitionPriorityComputerImplTest {

    @Test
    public void computeTest1() {
        Transition a = new Transition("a", Collections.emptyList(), Collections.emptyList());
        Transition b = new Transition("b", Collections.emptyList(), Collections.emptyList());
        Transition c = new Transition("c", Collections.emptyList(), Collections.emptyList());
        Transition d = new Transition("d", Collections.emptyList(), Collections.emptyList());
        Set<ImmutablePair<Transition, Transition>> priorityRelation = new HashSet<>(Arrays.asList(
                        ImmutablePair.of(d, b),
                        ImmutablePair.of(c, b)
                ));
        TransitionPriorityComputerImpl transitionPriorityComputer = new TransitionPriorityComputerImpl();
        Map<Transition, Integer> priorities =
                transitionPriorityComputer.compute(priorityRelation, Arrays.asList(a, b, c, d));
        assertEquals(4, priorities.size());
        assertTrue(priorities.get(d) < priorities.get(b));
        assertTrue(priorities.get(c) < priorities.get(b));
    }

    @Test
    public void computeTransitiveTest() {
        Transition a = new Transition("a", Collections.emptyList(), Collections.emptyList());
        Transition b = new Transition("b", Collections.emptyList(), Collections.emptyList());
        Transition c = new Transition("c", Collections.emptyList(), Collections.emptyList());
        Set<ImmutablePair<Transition, Transition>> priorityRelation = new HashSet<>(Arrays.asList(
                ImmutablePair.of(a, b),
                ImmutablePair.of(b, c)
        ));
        TransitionPriorityComputerImpl transitionPriorityComputer = new TransitionPriorityComputerImpl();
        Map<Transition, Integer> priorities =
                transitionPriorityComputer.compute(priorityRelation, Arrays.asList(a, b, c));
        assertEquals(3, priorities.size());
        assertTrue(priorities.get(a) < priorities.get(b));
        assertTrue(priorities.get(b) < priorities.get(c));
    }

    @Test
    public void computeTest2() {
        Transition a = new Transition("a", Collections.emptyList(), Collections.emptyList());
        Transition b = new Transition("b", Collections.emptyList(), Collections.emptyList());
        Transition c = new Transition("c", Collections.emptyList(), Collections.emptyList());
        Set<ImmutablePair<Transition, Transition>> priorityRelation = new HashSet<>(Arrays.asList(
                ImmutablePair.of(a, c),
                ImmutablePair.of(b, c),
                ImmutablePair.of(a, b)
        ));
        TransitionPriorityComputerImpl transitionPriorityComputer = new TransitionPriorityComputerImpl();
        Map<Transition, Integer> priorities =
                transitionPriorityComputer.compute(priorityRelation, Arrays.asList(a, b, c));
        assertEquals(3, priorities.size());
        assertTrue(priorities.get(a) < priorities.get(c));
        assertTrue(priorities.get(b) < priorities.get(c));
        assertTrue(priorities.get(a) < priorities.get(b));
    }

    @Test
    public void computeTest3() {
        Transition a = new Transition("a", Collections.emptyList(), Collections.emptyList());
        Transition b = new Transition("b", Collections.emptyList(), Collections.emptyList());
        Transition c = new Transition("c", Collections.emptyList(), Collections.emptyList());
        Transition d = new Transition("d", Collections.emptyList(), Collections.emptyList());
        Set<ImmutablePair<Transition, Transition>> priorityRelation = new HashSet<>(Arrays.asList(
                ImmutablePair.of(a, c),
                ImmutablePair.of(b, d),
                ImmutablePair.of(a, d)
                ));
        TransitionPriorityComputerImpl transitionPriorityComputer = new TransitionPriorityComputerImpl();
        Map<Transition, Integer> priorities =
                transitionPriorityComputer.compute(priorityRelation, Arrays.asList(a, b, c, d));
        assertEquals(4, priorities.size());
        assertTrue(priorities.get(a) < priorities.get(c));
        assertTrue(priorities.get(b) < priorities.get(d));
        assertTrue(priorities.get(a) < priorities.get(d));
    }

    @Test(expected = TransitionPriorityComputationException.class)
    public void computeErrorTest() {
        Transition a = new Transition("a", Collections.emptyList(), Collections.emptyList());
        Transition b = new Transition("b", Collections.emptyList(), Collections.emptyList());
        Transition c = new Transition("c", Collections.emptyList(), Collections.emptyList());
        Set<ImmutablePair<Transition, Transition>> priorityRelation = new HashSet<>(Arrays.asList(
                ImmutablePair.of(b, a),
                ImmutablePair.of(a, c),
                ImmutablePair.of(c, b)
        ));
        TransitionPriorityComputerImpl transitionPriorityComputer = new TransitionPriorityComputerImpl();
        transitionPriorityComputer.compute(priorityRelation, Arrays.asList(a, b, c));
    }
}