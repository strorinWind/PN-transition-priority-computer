package ru.hse.tpc.common;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class TransitionTest {

    @Test
    public void equalHashCodeTest() {
        Transition t1 = new Transition("t1", Arrays.asList(Pair.of(1,1), Pair.of(0, 2)),
                Collections.singletonList(Pair.of(1, 2)));
        Transition t2 = new Transition("t1", Arrays.asList(Pair.of(1,1), Pair.of(0, 2)),
                Collections.singletonList(Pair.of(1, 2)));
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    public void equalsTest() {
        Transition t1 = new Transition("t1", Arrays.asList(Pair.of(1,1), Pair.of(0, 2)),
                Collections.singletonList(Pair.of(1, 2)));
        Transition t2 = new Transition("t1", Arrays.asList(Pair.of(1,1), Pair.of(0, 2)),
                Collections.singletonList(Pair.of(1, 2)));
        assertEquals(t1, t2);
    }

    @Test
    public void equalByUnorderedPreListTest() {
        Transition t1 = new Transition("t1", Arrays.asList(Pair.of(1,1), Pair.of(0, 2)),
                Collections.singletonList(Pair.of(1, 2)));
        Transition t2 = new Transition("t1", Arrays.asList(Pair.of(0,2), Pair.of(1, 1)),
                Collections.singletonList(Pair.of(1, 2)));
        assertEquals(t1, t2);
    }

    @Test
    public void notEqualByPreListTest() {
        Transition t1 = new Transition("t1", Arrays.asList(Pair.of(1,2), Pair.of(0, 2)),
                Collections.singletonList(Pair.of(1, 2)));
        Transition t2 = new Transition("t1", Arrays.asList(Pair.of(0,2), Pair.of(1, 1)),
                Collections.singletonList(Pair.of(1, 2)));
        assertNotEquals(t1, t2);
    }

    @Test
    public void notEqualByLabelTest() {
        Transition t1 = new Transition("t1", Arrays.asList(Pair.of(1,1), Pair.of(0, 2)),
                Collections.singletonList(Pair.of(1, 2)));
        Transition t2 = new Transition("t2", Arrays.asList(Pair.of(1,1), Pair.of(0, 2)),
                Collections.singletonList(Pair.of(1, 2)));
        assertNotEquals(t1, t2);
    }

    @Test
    public void notEqualByPostListTest() {
        Transition t1 = new Transition("t1", Arrays.asList(Pair.of(1,1), Pair.of(0, 2)),
                Collections.singletonList(Pair.of(1, 2)));
        Transition t2 = new Transition("t1", Arrays.asList(Pair.of(1,1), Pair.of(0, 2)),
                Arrays.asList(Pair.of(1, 2), Pair.of(0, 2)));
        assertNotEquals(t1, t2);
    }
}