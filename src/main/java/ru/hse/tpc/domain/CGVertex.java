package ru.hse.tpc.domain;

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
