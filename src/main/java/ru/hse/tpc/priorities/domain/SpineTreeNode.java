package ru.hse.tpc.priorities.domain;

import ru.hse.tpc.desel.domain.Marking;
import ru.hse.tpc.desel.domain.Transition;

import java.util.HashMap;
import java.util.Map;

public class SpineTreeNode {

    private final Marking m;
    private final Transition incT;
    private final SpineTreeNode parent;
    private final Map<Transition, SpineTreeNode> childNodes = new HashMap<>();

    public SpineTreeNode(Marking m) {
        this(m, null, null);
    }

    public SpineTreeNode(Marking m, Transition incT, SpineTreeNode parent) {
        this.m = m;
        this.incT = incT;
        this.parent = parent;
    }

    public Marking getM() {
        return m;
    }

    public Transition getIncT() {
        return incT;
    }

    public SpineTreeNode getParent() {
        return parent;
    }

    public void addChildNode(Transition t, SpineTreeNode node) {
        childNodes.put(t, node);
    }

    public boolean containsOutgoingTransition(Transition t) {
        return childNodes.containsKey(t);
    }

    public SpineTreeNode getNextNode(Transition t) {
        return childNodes.get(t);
    }

    @Override
    public String toString() {
        return toString(1);
    }

    private String toString(int offset) {
        StringBuilder sb = new StringBuilder(m.toString()).append("\n");
        for (Map.Entry<Transition, SpineTreeNode> child : childNodes.entrySet()) {
            for (int i = 0; i < offset; i++) {
                sb.append("  ");
            }
            sb.append("-").append(child.getKey()).append("->").append(child.getValue().toString(offset+1));
        }
        return sb.toString();
    }
}
