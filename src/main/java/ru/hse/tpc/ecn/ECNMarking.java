package ru.hse.tpc.ecn;

import ru.hse.tpc.domain.Marking;

import java.util.HashMap;
import java.util.Map;

public class ECNMarking {

    private final Marking originalPlace;
    // place -> marking
    private final Map<Integer, Integer> additionalPlace;

    private final int hashCode;

    public ECNMarking(Marking originalPlace, Map<Integer, Integer> additionalPlace) {
        this.originalPlace = originalPlace;
        this.additionalPlace = new HashMap<>(additionalPlace);
        this.hashCode = precomputeHashCode();
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

    // TODO: change - if marking is omega check corresponding additional markings
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ECNMarking) || o.hashCode() != this.hashCode) {
            return false;
        }
        ECNMarking that = (ECNMarking) o;
        return this.additionalPlace.equals(that.additionalPlace) && this.originalPlace.equals(that.originalPlace);
    }
}
