package ru.hse.tpc.domain;

import org.apache.commons.lang3.tuple.Pair;
import ru.hse.tpc.cg.CGVertex;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Transition {

    private final String label;
    private final List<Pair<Integer, Integer>> preList;
    // key = place, value = post(t,place) - pre(t,place)
    private final Map<Integer, Integer> occurrenceResult;

    public Transition(String label, List<Pair<Integer, Integer>> preList, List<Pair<Integer, Integer>> postList) {
        this.label = label;
        this.preList = preList;
        Map<Integer, Integer> postMap = postList.stream().collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        this.occurrenceResult = preList.stream().collect(Collectors.toMap(
                Pair::getLeft,
                pre ->  -pre.getRight() + postMap.getOrDefault(pre.getLeft(), 0))
        );
        postMap.forEach((k, v) -> occurrenceResult.merge(k, v, (v1, v2) -> v1));
    }

    public boolean canOccur(Marking m) {
        for (Pair<Integer, Integer> pre : preList) {
            int placeMarking = m.getMarking(pre.getLeft());
            if (placeMarking != Marking.OMEGA && placeMarking < pre.getRight()) {
                return false;
            }
        }
        return true;
    }

//    public CGVertex fire(CGVertex sourceVertex) {
//        Marking sourceM = sourceVertex.getM();
//        Marking newMarking = new Marking(sourceM, occurrenceResult);
//        return new CGVertex(generalize(newMarking, sourceVertex), sourceVertex);
//    }

    public Marking fire(Marking m) {
        return new Marking(m, occurrenceResult);
    }

    // move this logic outside of transition
    private Marking generalize(Marking marking, CGVertex parentVertex) {
        CGVertex v = parentVertex;
        while (v != null) {
            Optional<Set<Integer>> placesO = v.getM().returnPlacesToGeneralizeIfStrictlyCoveredBy(marking);
            if (placesO.isPresent()) {
                return new Marking(marking, placesO.get());
            }
            v = v.getParent();
        }
        return marking;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
