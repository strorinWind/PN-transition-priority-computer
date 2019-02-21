package ru.hse.tpc.desel.ecn;

import ru.hse.tpc.desel.domain.Marking;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ECNMarking {

    private final Marking originalPlace;
    // place -> marking
    private final Map<Integer, Integer> additionalPlace;

    private final int hashCode;

    public ECNMarking(Marking originalPlace, Map<Integer, Integer> additionalPlace) {
        this.originalPlace = originalPlace;
        this.additionalPlace = Collections.unmodifiableMap(new HashMap<>(additionalPlace));
        this.hashCode = precomputeHashCode();
    }

    public Marking getOriginalPlace() {
        return originalPlace;
    }

    public Map<Integer, Integer> getAdditionalPlace() {
        return additionalPlace;
    }

    private int precomputeHashCode() {
        int result = additionalPlace.hashCode();
        int idx = 0;
        for (int m : originalPlace) {
            if (!additionalPlace.containsKey(idx)) {
                result = 31 * result + m;
            }
            idx++;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ECNMarking) || o.hashCode() != this.hashCode) {
            return false;
        }
        ECNMarking that = (ECNMarking) o;
        return this.additionalPlace.equals(that.additionalPlace) && this.originalPlace.equalsByBoundedPlaces(that.originalPlace);
    }

    @Override
    public String toString() {
        return "Original: " +
                originalPlace +
                " Additional: " +
                additionalPlace.entrySet().stream().map(e -> e.getKey() + " -> " + e.getValue()).collect(Collectors.joining("; "));
    }
}
