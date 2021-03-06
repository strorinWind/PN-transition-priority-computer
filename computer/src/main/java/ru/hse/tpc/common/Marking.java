package ru.hse.tpc.common;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.*;

/**
 * Represents Petri net marking
 */
public class Marking implements Iterable<Integer> {

    public static int OMEGA = -1;

    private final int[] marking;

    @JsonCreator
    public Marking(int... marking) {
        this.marking = new int[marking.length];
        System.arraycopy(marking, 0, this.marking, 0, marking.length);
    }

    public Marking(Marking sourceMarking, Map<Integer, Integer> transitionResult) {
        this.marking = new int[sourceMarking.marking.length];
        for (int i = 0; i < this.marking.length; i++) {
            if (sourceMarking.getMarking(i) != OMEGA){
                this.marking[i] = sourceMarking.getMarking(i) + transitionResult.getOrDefault(i, 0);
            } else {
                this.marking[i] = OMEGA;
            }
        }
    }

    public Marking(Marking nonGeneralizedMarking, Set<Integer> placesToGeneralize) {
        this.marking = new int[nonGeneralizedMarking.marking.length];
        for (int i = 0; i < this.marking.length; i++) {
            this.marking[i] = placesToGeneralize.contains(i) ? OMEGA : nonGeneralizedMarking.getMarking(i);
        }
    }

    public int getMarking(int place) {
        return marking[place];
    }

    /**
     * Forms list of places for which the marking should be generalized in case this marking < that marking.
     * @param thatM - another marking which occurred later in the sequence than this marking
     * @return list of places if this marking is strictly covered by that
     */
    public Optional<Set<Integer>> returnPlacesToGeneralizeIfStrictlyCoveredBy(Marking thatM) {
        Set<Integer> result = new HashSet<>(this.marking.length);
        for (int i = 0; i < this.marking.length; i++) {
            int thisPlaceM = this.marking[i];
            int thatPlaceM = thatM.getMarking(i);
            if (thatPlaceM == OMEGA) {
                continue;
            }
            if (thisPlaceM == OMEGA || thisPlaceM > thatPlaceM) {
                return Optional.empty();
            } else if (thisPlaceM < thatPlaceM) {
                result.add(i);
            }
        }
        return Optional.of(result);
    }

    /**
     * Compares this non-generalized marking to that non-generalized marking to check if this is strictly covered
     * by that.
     * Note: the both markings should be non-generalized, since generalized places are not accounted for.
     * @param thatM - non-generalized marking
     * @return - true, if this is strictly covered by that
     */
    public boolean isStrictlyCoveredBy(Marking thatM) {
        boolean isAtLeastOnePlaceStrictlyCovered = false;
        for (int i = 0; i < this.marking.length; i++) {
            int thisPlaceM = this.marking[i];
            int thatPlaceM = thatM.marking[i];
            if (thisPlaceM > thatPlaceM) {
                return false;
            } else if (thisPlaceM < thatPlaceM) {
                isAtLeastOnePlaceStrictlyCovered = true;
            }
        }
        return isAtLeastOnePlaceStrictlyCovered;
    }

    public boolean equalsByBoundedPlaces(Marking anotherMarking) {
        for (int i = 0; i < this.marking.length; i++) {
            if (anotherMarking.marking[i] != this.marking[i] &&
                    (anotherMarking.marking[i] != OMEGA && this.marking[i] != OMEGA)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object anotherMarking) {
        if (!(anotherMarking instanceof Marking)) {
            return false;
        }
        Marking that = (Marking) anotherMarking;
        for (int i = 0; i < this.marking.length; i++) {
            if (that.marking[i] != this.marking[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = this.marking[0];
        for (int i = 1; i < this.marking.length; i++) {
            result = 31 * result + this.marking[i];
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder strB = new StringBuilder("(");
        for (int pMarking : this.marking) {
            if (pMarking == OMEGA) {
                strB.append("\u03C9");
            } else {
                strB.append(pMarking);
            }
            strB.append(",");
        }
        return strB.deleteCharAt(strB.length() - 1).append(")").toString();
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private int idx = 0;

            @Override
            public boolean hasNext() {
                return idx < marking.length;
            }

            @Override
            public Integer next() {
                return marking[idx++];
            }
        };
    }
}
