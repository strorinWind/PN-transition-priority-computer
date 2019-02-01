package ru.hse.tpc.domain;

import org.junit.Test;

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
}