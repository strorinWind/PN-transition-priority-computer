package ru.hse.tpc.desel;

import ru.hse.tpc.common.Marking;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractDeselAlgo implements DeselAlgo {

    protected Set<Integer> findUnboundedPlaces(Set<Marking> markings) {
        Set<Integer> result = new HashSet<>();
        for (Marking m : markings) {
            int curPlace = 0;
            for (int pm : m) {
                if (pm == Marking.OMEGA) {
                    result.add(curPlace);
                }
                curPlace++;
            }
        }
        return result;
    }

}
