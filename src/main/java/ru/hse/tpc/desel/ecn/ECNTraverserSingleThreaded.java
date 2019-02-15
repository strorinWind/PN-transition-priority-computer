package ru.hse.tpc.desel.ecn;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.desel.domain.CyclicRun;
import ru.hse.tpc.desel.domain.Marking;
import ru.hse.tpc.desel.domain.Transition;

import java.util.*;
import java.util.stream.Collectors;

public class ECNTraverserSingleThreaded implements ECNTraverser {

    // Stop traverse for a sequence if for two ECNMarkings the originalPlaces are the same
    @Override
    public List<CyclicRun> findCyclicRuns(Map<Marking, Set<ImmutablePair<Transition, Marking>>> cg,
                                          ECNMarking initialMarking) {
        List<CyclicRun> cyclicRuns = new ArrayList<>();
        TraverseNode root = new TraverseNode(initialMarking, null, null);
        Queue<ImmutablePair<TraverseNode, ImmutablePair<Transition, Marking>>> workQ =
                cg.get(initialMarking.getOriginalPlace()).stream()
                        .map(outgoingMarking -> ImmutablePair.of(root, outgoingMarking))
                        .collect(Collectors.toCollection(LinkedList::new));
        while (!workQ.isEmpty()) {
            ImmutablePair<TraverseNode, ImmutablePair<Transition, Marking>> workUnit = workQ.remove();
            TraverseNode parentNode = workUnit.left;
            ECNMarking parentECNMarking = parentNode.getMarking();
            Transition outgoingTransition = workUnit.right.left;
            Marking resultingGeneralizedMarking = workUnit.right.right;

            ECNMarking newECNMarking = computeECNMarking(parentECNMarking, outgoingTransition, resultingGeneralizedMarking);
            Optional<TraverseNode> ancestorWithDupOriginalPlacesO = getAncestorWithDupOriginalPlaces(parentNode, newECNMarking);
            if (ancestorWithDupOriginalPlacesO.isPresent()) {
                System.out.println("Cycle for original places marking detected: " + newECNMarking);
                Optional<TraverseNode> cycleStartO = getCycleStart(ancestorWithDupOriginalPlacesO.get(), newECNMarking);
                if (cycleStartO.isPresent()) {
                    System.out.println("Cyclic run detected");
                    TraverseNode cycleStart = cycleStartO.get();
                    cyclicRuns.add(new CyclicRun(buildCyclePrefix(cycleStart), buildCycle(cycleStart, parentNode, outgoingTransition)));
                }
            } else {
                TraverseNode newNode = new TraverseNode(newECNMarking, parentNode, outgoingTransition);
                workQ.addAll(
                  cg.get(newECNMarking.getOriginalPlace()).stream()
                          .map(outgoingMarking -> ImmutablePair.of(newNode, outgoingMarking)).collect(Collectors.toList())
                );
            }
        }

        return cyclicRuns;
    }

    private ECNMarking computeECNMarking(ECNMarking parentMarking, Transition t, Marking childGeneralizedMarking) {
        Map<Integer, Integer> newAdditionalPlacesMarking = t.fireForSpecificPlaces(parentMarking.getAdditionalPlace());
        return new ECNMarking(childGeneralizedMarking, newAdditionalPlacesMarking);
    }

    private Optional<TraverseNode> getAncestorWithDupOriginalPlaces(TraverseNode parentNode, ECNMarking marking) {
        TraverseNode curNode = parentNode;
        while (curNode != null) {
            if (curNode.getMarking().equalsByOriginalPlaces(marking)) {
                return Optional.of(curNode);
            }
            curNode = curNode.getParent();
        }
        return Optional.empty();
    }

    private Optional<TraverseNode> getCycleStart(TraverseNode parentNode, ECNMarking marking) {
        TraverseNode curNode = parentNode;
        while (curNode != null) {
            ECNMarking ancestorMarking = curNode.getMarking();
            if (ancestorMarking.equalsByOriginalPlaces(marking) && ancestorMarking.equalsByAdditionalPlaces(marking)) {
                return Optional.of(curNode);
            }
            curNode = curNode.getParent();
        }
        return Optional.empty();
    }

    private Collection<Transition> buildCyclePrefix(TraverseNode cycleStart) {
        TraverseNode parentNode = cycleStart.getParent();
        Transition incomingTransition = cycleStart.getIncomingTransition();
        Deque<Transition> result = new LinkedList<>();
        while (parentNode != null) {
            result.push(incomingTransition);
            parentNode = parentNode.getParent();
            incomingTransition = parentNode.getIncomingTransition();
        }
        return result;
    }

    private Collection<Transition> buildCycle(TraverseNode cycleStart, TraverseNode edgeCycleNode, Transition closingTransition) {
        Deque<Transition> result = new LinkedList<>();
        result.push(closingTransition);
        TraverseNode curNode = edgeCycleNode;
        while (cycleStart != curNode) {
            result.push(curNode.getIncomingTransition());
            curNode = curNode.getParent();
        }
        return result;
    }
}
