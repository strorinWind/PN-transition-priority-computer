package ru.hse.tpc.priorities;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;
import ru.hse.tpc.priorities.common.SpineTreeNode;
import ru.hse.tpc.priorities.computer.TransitionPriorityComputer;
import ru.hse.tpc.priorities.computer.TransitionPriorityComputerImpl;
import ru.hse.tpc.priorities.pr.PriorityRelationComputer;
import ru.hse.tpc.priorities.pr.PriorityRelationComputerImpl;
import ru.hse.tpc.priorities.spct.SPCTBuilder;
import ru.hse.tpc.priorities.spct.SPCTBuilderImpl;
import ru.hse.tpc.priorities.st.SpineTreeBuilder;
import ru.hse.tpc.priorities.st.SpineTreeBuilderImpl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Entry point for priorities computation
 */
public class PrioritiesComputationAlgo {

    public static Map<Transition, Integer> computePriorityValues(List<CyclicRun> cyclicRuns, Marking initialMarking,
                                                                 List<Transition> transitions) {
        SpineTreeBuilder spineTreeBuilder = new SpineTreeBuilderImpl();
        SpineTreeNode spineTree = spineTreeBuilder.build(initialMarking, cyclicRuns);
        // <DEBUG>
        System.out.println("Spine tree:");
        System.out.println(spineTree);
        // </DEBUG>
        SPCTBuilder spctBuilder = new SPCTBuilderImpl();
        SpineTreeNode spct = spctBuilder.build(spineTree, transitions);
        // <DEBUG>
        System.out.println("Spine based covering tree:");
        System.out.println(spct);
        // </DEBUG>
        PriorityRelationComputer priorityRelationComputer = new PriorityRelationComputerImpl();
        Set<ImmutablePair<Transition, Transition>> pr = priorityRelationComputer.compute(spct);
        Set<ImmutablePair<Transition, Transition>> contradictions = pr.stream()
                .filter(r -> pr.contains(ImmutablePair.of(r.right, r.left))).collect(Collectors.toSet());
        if (!contradictions.isEmpty()) {
            System.out.println("Priority relation contains the following contradicting relations:");
            contradictions.forEach(r -> System.out.print("(" + r.left + "," + r.right + ")" + " "));
            System.out.println();
            System.out.println("Removing them from further processing...");
            pr.removeAll(contradictions);
        }
        // <DEBUG>
        System.out.println("Priority relations:");
        pr.forEach(r -> System.out.print("(" + r.left + "," + r.right + ")" + " "));
        System.out.println();
        // </DEBUG>
        TransitionPriorityComputer transitionPriorityComputer = new TransitionPriorityComputerImpl();
        return transitionPriorityComputer.compute(pr, transitions);
    }

}
