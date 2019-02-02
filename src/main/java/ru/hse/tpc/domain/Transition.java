package ru.hse.tpc.domain;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Transition {

    private final String label;
    private final List<Pair<Integer, Integer>> preList;
    // key = place, value = post(t,place) - pre(t,place)
    private final Map<Integer, Integer> occurenceResult;

    public Transition(String label, List<Pair<Integer, Integer>> preList, List<Pair<Integer, Integer>> postList) {
        this.label = label;
        this.preList = preList;
        Map<Integer, Integer> postMap = postList.stream().collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        this.occurenceResult = preList.stream().collect(Collectors.toMap(
                Pair::getLeft,
                pre ->  -pre.getRight() + postMap.getOrDefault(pre.getLeft(), 0))
        );
    }

    public boolean canOccure(Marking m) {
        for (Pair<Integer, Integer> pre : preList) {
            int placeMarking = m.getMarking(pre.getLeft());
            if (placeMarking != Marking.OMEGA && placeMarking < pre.getRight()) {
                return false;
            }
        }
        return true;
    }

    public CGVertex fire(CGVertex sourceVertex) {
        Marking sourceM = sourceVertex.getM();
        Marking newMarking = new Marking(sourceM, occurenceResult);
        return new CGVertex(generalize(newMarking, sourceVertex), sourceVertex);
    }

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
}
