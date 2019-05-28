package ru.hse.tpc.priorities;

import com.google.common.flogger.FluentLogger;
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

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    public static Map<Transition, Integer> computePriorityValues(List<CyclicRun> cyclicRuns, Marking initialMarking,
                                                                 List<Transition> transitions) {
        logger.atInfo().log("Building spine tree upon %d cycles", cyclicRuns.size());
        SpineTreeBuilder spineTreeBuilder = new SpineTreeBuilderImpl();
        SpineTreeNode spineTree = spineTreeBuilder.build(initialMarking, cyclicRuns);
        SPCTBuilder spctBuilder = new SPCTBuilderImpl();
        SpineTreeNode spct = spctBuilder.build(spineTree, transitions);
        PriorityRelationComputer priorityRelationComputer = new PriorityRelationComputerImpl();
        Set<ImmutablePair<Transition, Transition>> pr = priorityRelationComputer.compute(spct);
        Set<ImmutablePair<Transition, Transition>> contradictions = pr.stream()
                .filter(r -> pr.contains(ImmutablePair.of(r.right, r.left))).collect(Collectors.toSet());
        if (!contradictions.isEmpty()) {
            logger.atWarning().log("Priority relation contains the following contradicting relations:\n%s\n" +
                            "Removing them from further processing...",
                    contradictions.stream().map(r -> "(" + r.left + "," + r.right + ")").collect(Collectors.joining("\n")));
            pr.removeAll(contradictions);
        }
        if (!pr.isEmpty()) {
            logger.atInfo().log("Priority relation:\n%s",
                    pr.stream().map(r -> "(" + r.left + "," + r.right + ")").collect(Collectors.joining("\n")));
        } else {
            logger.atInfo().log("Priority relation is empty");
        }
        TransitionPriorityComputer transitionPriorityComputer = new TransitionPriorityComputerImpl();
        return transitionPriorityComputer.compute(pr, transitions);
    }

}
