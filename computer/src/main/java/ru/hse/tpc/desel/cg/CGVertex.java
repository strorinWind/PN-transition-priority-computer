package ru.hse.tpc.desel.cg;

import ru.hse.tpc.common.Marking;

/**
 * Represents a vertex of covering graph
 */
public class CGVertex {

    private final Marking m;
    private final CGVertex parent;

    public CGVertex(Marking m, CGVertex parent) {
        this.m = m;
        this.parent = parent;
    }

    public Marking getM() {
        return m;
    }

    public CGVertex getParent() {
        return parent;
    }
}
