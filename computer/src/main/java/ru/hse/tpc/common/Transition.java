package ru.hse.tpc.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents Petri net transition
 */
public class Transition {

    private final String label;
    // list of pairs (place, pre(t, place))
    private final List<Pair<Integer, Integer>> preList;
    // key = place, value = post(t,place) - pre(t,place)
    private final Map<Integer, Integer> occurrenceResult;

    private final int hashCode;

    @JsonCreator
    public Transition(@JsonProperty("label") String label,
                      @JsonProperty("preList") List<Pair<Integer, Integer>> preList,
                      @JsonProperty("postList") List<Pair<Integer, Integer>> postList) {
        if (preList == null) {
            preList = new ArrayList<>();
        }
        if (postList == null) {
            postList = new ArrayList<>();
        }
        
        this.label = label;
        this.preList = preList;
        Map<Integer, Integer> postMap = postList.stream().collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        this.occurrenceResult = preList.stream().collect(Collectors.toMap(
                Pair::getLeft,
                pre ->  -pre.getRight() + postMap.getOrDefault(pre.getLeft(), 0))
        );
        postMap.forEach((k, v) -> occurrenceResult.merge(k, v, (v1, v2) -> v1));
        this.hashCode = precomputeHashCode();
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

    public Marking fire(Marking m) {
        return new Marking(m, occurrenceResult);
    }

    public Map<Integer, Integer> fireForSpecificPlaces(Map<Integer, Integer> placeToMarkingMap) {
        return placeToMarkingMap.entrySet().stream()
                .map(e -> ImmutablePair.of(e.getKey(), e.getValue() + occurrenceResult.getOrDefault(e.getKey(), 0)))
                .collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }

    private int precomputeHashCode() {
        int result = Objects.hashCode(label);
        for (Pair<Integer, Integer> p : preList) {
            result += (124567890 + p.hashCode()) * p.hashCode();
        }
        result = 31 * result + occurrenceResult.entrySet().hashCode();
        return result;
    }

    public List<Pair<Integer, Integer>> getPreList() {
        return preList;
    }

    public Map<Integer, Integer> getOccurrenceResult() {
        return occurrenceResult;
    }

    @Nullable
    public Pair<Integer, Integer> getPreList(int id) {
        for (Pair<Integer, Integer> integerIntegerPair : preList) {
            if (integerIntegerPair.getLeft() == id) {
                return integerIntegerPair;
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Transition) || o.hashCode() != this.hashCode) {
            return false;
        }
        Transition that = (Transition) o;
        return this.label.equals(that.label) &&
                this.preList.size() == that.preList.size() &&
                new HashSet<>(this.preList).containsAll(that.preList) &&
                this.occurrenceResult.equals(that.occurrenceResult);
    }

    @Override
    public String toString() {
        return this.label;
    }
}
