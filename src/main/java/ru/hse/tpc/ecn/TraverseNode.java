package ru.hse.tpc.ecn;

public class TraverseNode {

    private final ECNMarking marking;
    private final TraverseNode parent;

    public TraverseNode(ECNMarking marking, TraverseNode parent) {
        this.marking = marking;
        this.parent = parent;
    }

    public ECNMarking getMarking() {
        return marking;
    }

    public TraverseNode getParent() {
        return parent;
    }
}
