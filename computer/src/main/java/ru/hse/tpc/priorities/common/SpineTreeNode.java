package ru.hse.tpc.priorities.common;

import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a spine tree node
 */
public class SpineTreeNode {

    private final Marking m;
    private final Transition incT;
    private final SpineTreeNode parent;
    private final Map<Transition, SpineTreeNode> childNodes = new HashMap<>();
    private NodeColor color = NodeColor.NOT_COLORED;

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

    public boolean isLeaf() {
        return childNodes.isEmpty();
    }

    public Collection<SpineTreeNode> getChildNodes() {
        return childNodes.values();
    }

    public boolean containsOutgoingTransition(Transition t) {
        return childNodes.containsKey(t);
    }

    public SpineTreeNode getNextNode(Transition t) {
        return childNodes.get(t);
    }

    public void color(NodeColor color) {
        this.color = color;
    }

    public NodeColor getColor() {
        return color;
    }

    @Override
    public String toString() {
        return toString(1);
    }

    private String toString(int offset) {
        StringBuilder sb = new StringBuilder(m.toString());
        if (color != NodeColor.NOT_COLORED) {
            sb.append(" ").append(color);
        }
        sb.append("\n");
        for (Map.Entry<Transition, SpineTreeNode> child : childNodes.entrySet()) {
            for (int i = 0; i < offset; i++) {
                sb.append("  ");
            }
            sb.append("-").append(child.getKey()).append("->").append(child.getValue().toString(offset+1));
        }
        return sb.toString();
    }
}
