package ru.hse.tpc.cg;

import ru.hse.tpc.domain.Marking;

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
