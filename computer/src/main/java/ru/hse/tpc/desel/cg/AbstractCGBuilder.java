package ru.hse.tpc.desel.cg;

import ru.hse.tpc.common.Marking;

import java.util.Optional;
import java.util.Set;

abstract class AbstractCGBuilder implements CGBuilder {

    Marking generalize(Marking marking, CGVertex parentVertex) {
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

}
