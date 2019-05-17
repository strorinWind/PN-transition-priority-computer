package ru.hse.tpc.desel.ecn;

import ru.hse.tpc.common.Transition;

public class TraverseNode {

    private final int pathSize;

    private final TraverseNode parent;
    private final Transition incT;
    private final ECNMarking marking;

    public TraverseNode(ECNMarking marking) {
        this(null, null, marking);
    }

    public TraverseNode(TraverseNode parent, Transition incT, ECNMarking marking) {
        this.parent = parent;
        this.incT = incT;
        this.marking = marking;
        this.pathSize = (parent == null) ? 0 : parent.pathSize + 1;
    }

    public TraverseNode getParent() {
        return parent;
    }

    public Transition getIncT() {
        return incT;
    }

    public ECNMarking getMarking() {
        return marking;
    }

    public int getPathSize() {
        return pathSize;
    }
}
