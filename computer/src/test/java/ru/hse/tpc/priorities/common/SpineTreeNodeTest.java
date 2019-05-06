package ru.hse.tpc.priorities.common;

import org.junit.Test;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;

import java.util.Collections;

public class SpineTreeNodeTest {

    @Test
    public void toStringTest() {
        Transition a = new Transition("a", Collections.emptyList(), Collections.emptyList());
        Transition b = new Transition("b", Collections.emptyList(), Collections.emptyList());
        Transition c = new Transition("c", Collections.emptyList(), Collections.emptyList());
        SpineTreeNode root = new SpineTreeNode(new Marking(1, 0));
        SpineTreeNode n11 = new SpineTreeNode(new Marking(0, 1), a, root);
        root.addChildNode(a, n11);
        SpineTreeNode n12 = new SpineTreeNode(new Marking(1, 1), b, root);
        root.addChildNode(b, n12);
        SpineTreeNode n21 = new SpineTreeNode(new Marking(1, 2), c, n11);
        n11.addChildNode(c, n21);
        System.out.println(root);
    }
}