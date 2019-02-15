package ru.hse.tpc.desel.domain;

import org.junit.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class MarkingTest {

    @Test
    public void hashCodeTest() {
        Marking m1 = new Marking(1,1,0);
        Marking m2 = new Marking(1,0,1);
        Marking m3 = new Marking(0,1,1);

        Marking m4 = new Marking(1,0,0);
        Marking m5 = new Marking(0,1,0);
        Marking m6 = new Marking(0,0,1);

        Marking m7 = new Marking(1,1,1);

        System.out.println(m1 + " : " + m1.hashCode());
        System.out.println(m2 + " : " + m2.hashCode());
        System.out.println(m3 + " : " + m3.hashCode());

        System.out.println(m4 + " : " + m4.hashCode());
        System.out.println(m5 + " : " + m5.hashCode());
        System.out.println(m6 + " : " + m6.hashCode());

        System.out.println(m7 + " : " + m7.hashCode());
    }

    @Test
    public void equalsTest() {
        Marking m1 = new Marking(1,1,0);
        Marking m2 = new Marking(1,0,1);
        Marking m3 = new Marking(1,0,1);

        assertNotEquals(m1, m2);
        assertNotEquals(m1, null);
        assertNotEquals(m1, new Object());

        assertEquals(m2, m3);
    }

    @Test
    public void toStringTest() {
        Marking m1 = new Marking(-1,1,0,-1);
        System.out.println(m1);
    }

    @Test
    public void returnPlacesToGeneralizeIfStrictlyCoveredByTest() {
        Marking m1 = new Marking(0,  0);
        Marking m2 = new Marking(0,  1);
        Marking m3 = new Marking(0, -1);
        Marking m4 = new Marking(1, -1);
        Marking m5 = new Marking(1,  0);
        Marking m6 = new Marking(1,  1);

        Optional<Set<Integer>> r1O = m1.returnPlacesToGeneralizeIfStrictlyCoveredBy(m4);
        assertTrue(r1O.isPresent());
        assertEquals(1, r1O.get().size());
        assertTrue(r1O.get().contains(0));

        Optional<Set<Integer>> r2O = m2.returnPlacesToGeneralizeIfStrictlyCoveredBy(m4);
        assertTrue(r2O.isPresent());
        assertEquals(1, r2O.get().size());
        assertTrue(r2O.get().contains(0));

        Optional<Set<Integer>> r3O = m3.returnPlacesToGeneralizeIfStrictlyCoveredBy(m4);
        assertTrue(r3O.isPresent());
        assertEquals(1, r3O.get().size());
        assertTrue(r3O.get().contains(0));

        Optional<Set<Integer>> r4O = m1.returnPlacesToGeneralizeIfStrictlyCoveredBy(m5);
        assertTrue(r4O.isPresent());
        assertEquals(1, r4O.get().size());
        assertTrue(r4O.get().contains(0));

        Optional<Set<Integer>> r5O = m3.returnPlacesToGeneralizeIfStrictlyCoveredBy(m5);
        assertFalse(r5O.isPresent());

        Optional<Set<Integer>> r6O = m2.returnPlacesToGeneralizeIfStrictlyCoveredBy(m6);
        assertTrue(r6O.isPresent());
        assertEquals(1, r6O.get().size());
        assertTrue(r6O.get().contains(0));

        Optional<Set<Integer>> r7O = m3.returnPlacesToGeneralizeIfStrictlyCoveredBy(m6);
        assertFalse(r7O.isPresent());
    }
}