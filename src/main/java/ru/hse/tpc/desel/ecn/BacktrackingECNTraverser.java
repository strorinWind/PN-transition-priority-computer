package ru.hse.tpc.desel.ecn;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.desel.domain.CyclicRun;
import ru.hse.tpc.desel.domain.Marking;
import ru.hse.tpc.desel.domain.Transition;

import java.util.*;

public class BacktrackingECNTraverser implements ECNTraverser {

    @Override
    public List<CyclicRun> findCyclicRuns(Map<Marking, Set<ImmutablePair<Transition, Marking>>> cg,
                                          ECNMarking initialMarking) {
        List<CyclicRun> cyclicRuns = new ArrayList<>();
        // Stack of pairs (marking, incomingTransition)
        Deque<ImmutablePair<ECNMarking, Transition>> seq = new LinkedList<>();
        seq.push(ImmutablePair.of(initialMarking, null));
        // Stack of pairs (marking, adjacentMarkingWithLinkingTransition)
        Deque<ImmutablePair<Marking, ImmutablePair<Transition, Marking>>> firedTransitions = new LinkedList<>();
        Set<ECNMarking> occurredMarkings = new HashSet<>();
        occurredMarkings.add(initialMarking);
        while (!seq.isEmpty()) {
            ImmutablePair<ECNMarking, Transition> seqElem = seq.peek();
            Set<ImmutablePair<Transition, Marking>> notFiredTransitions = cg.get(seqElem.left.getOriginalPlace());
            if (notFiredTransitions.isEmpty()) {
                while (!firedTransitions.isEmpty() && firedTransitions.peek().left.equals(seqElem.left.getOriginalPlace())) {
                    notFiredTransitions.add(firedTransitions.pop().right);
                }
                occurredMarkings.remove(seqElem.left);
                seq.pop();
            } else {
                ImmutablePair<Transition, Marking> transitionToFire = notFiredTransitions.iterator().next();
                notFiredTransitions.remove(transitionToFire);
                firedTransitions.push(ImmutablePair.of(seqElem.left.getOriginalPlace(), transitionToFire));
                ECNMarking newEcnM = computeECNMarking(seqElem.left, transitionToFire.left, transitionToFire.right);
                if (occurredMarkings.contains(newEcnM)) {
                    cyclicRuns.add(buildCyclicRun(seq.descendingIterator(), newEcnM, transitionToFire.left));
                } else if (newEcnM.getAdditionalPlace().values().stream().allMatch(m -> m >= 0)) {
                    seq.push(ImmutablePair.of(newEcnM, transitionToFire.left));
                    occurredMarkings.add(newEcnM);
                }
            }
        }

        return cyclicRuns;
    }

    private ECNMarking computeECNMarking(ECNMarking parentMarking, Transition t, Marking childGeneralizedMarking) {
        Map<Integer, Integer> newAdditionalPlacesMarking = t.fireForSpecificPlaces(parentMarking.getAdditionalPlace());
        return new ECNMarking(childGeneralizedMarking, newAdditionalPlacesMarking);
    }

    private CyclicRun buildCyclicRun(Iterator<ImmutablePair<ECNMarking, Transition>> seqIter, ECNMarking edgeCycleMarking,
                                     Transition closingTransition) {
        List<Transition> prefix = new ArrayList<>();
        List<Transition> cycle = new ArrayList<>();
        boolean isCycleStarted = false;
        while (seqIter.hasNext()) {
            ImmutablePair<ECNMarking, Transition> seqElem = seqIter.next();
            if (isCycleStarted) {
                cycle.add(seqElem.right);
            } else {
                if (seqElem.right != null) {
                    prefix.add(seqElem.right);
                }
                isCycleStarted = seqElem.left.equals(edgeCycleMarking);
            }
        }
        cycle.add(closingTransition);
        return new CyclicRun(prefix, cycle);
    }
}
