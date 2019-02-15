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

    // TODO: double check because very possibly we need two absolutely equal markings not only by bounded places
    public boolean equalsByOriginalPlaces(ECNMarking anotherMarking) {
        return this.originalPlace.equalsByBoundedPlaces(anotherMarking.originalPlace);
    }

    public boolean equalsByAdditionalPlaces(ECNMarking anotherMarking) {
        return this.additionalPlace.equals(anotherMarking.additionalPlace);
    }

    private int precomputeHashCode() {
        int result = originalPlace.hashCode();
        result = 31 * result + additionalPlace.hashCode();
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
        return this.additionalPlace.equals(that.additionalPlace) && this.originalPlace.equals(that.originalPlace);
    }

    @Override
    public String toString() {
        return "Original: " +
                originalPlace +
                " Additional: " +
                additionalPlace.entrySet().stream().map(e -> e.getKey() + " -> " + e.getValue()).collect(Collectors.joining("; "));
    }
}
