package ru.hse.tpc;

import org.junit.Test;

import java.util.concurrent.Phaser;

public class PhaserTest {

    @Test
    public void testPhaser() {
        Phaser parent = new Phaser();
        Phaser child1 = new Phaser(parent);
        Phaser child2 = new Phaser(parent);
        child1.register();
        child2.register();

        System.out.println("Parent: "+parent.isTerminated());
        System.out.println("Child1: "+child1.isTerminated());
        System.out.println("Child2: "+child1.isTerminated()+"\n");

        child1.arriveAndDeregister();
        System.out.println("Parent: "+parent.isTerminated());
        System.out.println("Child1: "+child1.isTerminated());
        System.out.println("Child2: "+child2.isTerminated()+"\n");

        child2.arriveAndDeregister();
        System.out.println("Parent: "+parent.isTerminated());
        System.out.println("Child1: "+child1.isTerminated());
        System.out.println("Child2: "+child2.isTerminated()+"\n");
    }

}
