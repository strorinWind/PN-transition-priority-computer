package ru.hse.tpc.desel.ecn;

import ru.hse.tpc.desel.domain.Transition;

public class TraverseNode {

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
}
