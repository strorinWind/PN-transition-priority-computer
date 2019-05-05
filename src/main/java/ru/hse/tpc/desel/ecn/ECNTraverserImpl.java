package ru.hse.tpc.desel.ecn;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ECNTraverserImpl implements ECNTraverser {

    @Override
    public List<CyclicRun> findCyclicRuns(Map<Marking, List<ImmutablePair<Transition, Marking>>> cg,
                                          ECNMarking initialMarking, Set<Transition> transitionSet) {
        List<CyclicRun> cyclicRuns = new ArrayList<>();
        Deque<TraverseNode> traverseQ = new LinkedList<>();
        traverseQ.addFirst(new TraverseNode(initialMarking));
        while (!traverseQ.isEmpty()) {
            TraverseNode node = traverseQ.remove();
            Optional<CyclicRun> cyclicRunO = checkForCyclicRun(node);
            if (cyclicRunO.isPresent()) {
                if (cyclicRunContainsAllTransitions(transitionSet, cyclicRunO.get())) {
                    cyclicRuns.add(cyclicRunO.get());
                    continue;
                }
            }
            // generate new valid nodes (fire only transitions that haven't occurred in seq, filter those
            // with additional places < 0) and add to traverseQ
            List<ImmutablePair<Transition, Marking>> validTransitions =
                    filterOutOccurredTransitions(node, cg.get(node.getMarking().getOriginalPlace()));
            List<TraverseNode> newNodes = validTransitions.stream()
                    .map(toNewTraverseNode(node))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            traverseQ.addAll(newNodes);
        }

        return cyclicRuns;
    }

    protected Optional<CyclicRun> checkForCyclicRun(TraverseNode node) {
        Deque<Transition> prefix = new LinkedList<>();
        Deque<Transition> cycle = new LinkedList<>();
        Deque<Transition> targetPart = cycle;
        boolean hasCycleOccurred = false;
        ECNMarking marking = node.getMarking();
        targetPart.push(node.getIncT());
        TraverseNode curNode = node.getParent();
        while (curNode != null) {
            if (marking.equals(curNode.getMarking())) {
                hasCycleOccurred = true;
                targetPart = prefix;
            }
            if (curNode.getIncT() != null) {
                targetPart.push(curNode.getIncT());
            }
            curNode = curNode.getParent();
        }
        if (hasCycleOccurred) {
            return Optional.of(new CyclicRun(prefix, cycle));
        }
        return Optional.empty();
    }

    protected boolean cyclicRunContainsAllTransitions(Set<Transition> transitionSet, CyclicRun cyclicRun) {
        return new HashSet<>(cyclicRun.getCycle()).containsAll(transitionSet);
    }

    protected List<ImmutablePair<Transition, Marking>> filterOutOccurredTransitions(
            TraverseNode baseMarkingNode,
            List<ImmutablePair<Transition, Marking>> outgoingTransitions
    ) {
        Map<Transition, Marking> tToResultingM = outgoingTransitions.stream()
                .collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
        Marking baseMarking = baseMarkingNode.getMarking().getOriginalPlace();
        TraverseNode curNode = baseMarkingNode.getParent();
        Transition curT = baseMarkingNode.getIncT();
        while (curNode != null) {
            if (curNode.getMarking().getOriginalPlace().equals(baseMarking)) {
                tToResultingM.remove(curT);
            }
            curT = curNode.getIncT();
            curNode = curNode.getParent();
        }
        return tToResultingM.entrySet().stream().map(e -> ImmutablePair.of(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    protected Function<ImmutablePair<Transition, Marking>, Optional<TraverseNode>> toNewTraverseNode(TraverseNode parentNode) {
        ECNMarking parentMarking = parentNode.getMarking();
        return t -> {
            Map<Integer, Integer> newAdditionalPlacesMarking =
                    t.left.fireForSpecificPlaces(parentMarking.getAdditionalPlace());
            ECNMarking newECNMarking = new ECNMarking(t.right, newAdditionalPlacesMarking);
            if (!areAdditionalPlacesValid(newECNMarking)) {
                return Optional.empty();
            }
            return Optional.of(new TraverseNode(parentNode, t.left, newECNMarking));
        };
    }

    private boolean areAdditionalPlacesValid(ECNMarking marking) {
        return marking.getAdditionalPlace().values().stream().allMatch(m -> m >= 0);
    }
}
