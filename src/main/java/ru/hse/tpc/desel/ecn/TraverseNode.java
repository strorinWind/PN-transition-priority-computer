package ru.hse.tpc.desel.ecn;

import ru.hse.tpc.desel.domain.Transition;

public class TraverseNode {

    private final ECNMarking marking;
    private final TraverseNode parent;
    private final Transition incomingTransition;

    public TraverseNode(ECNMarking marking, TraverseNode parent, Transition incomingTransition) {
        this.marking = marking;
        this.parent = parent;
        this.incomingTransition = incomingTransition;
    }

    public ECNMarking getMarking() {
        return marking;
    }

    public TraverseNode getParent() {
        return parent;
    }

    public Transition getIncomingTransition() {
        return incomingTransition;
    }
}
