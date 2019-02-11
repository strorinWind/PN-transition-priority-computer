package ru.hse.tpc.ecn;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.domain.Marking;
import ru.hse.tpc.domain.Transition;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ECNTraverserSingleThreaded implements ECNTraverser {

    // Stop traverse for a sequence if for two ECNMarkings the originalPlaces are the same
    @Override
    public List<List<Transition>> findCyclicRuns(Map<Marking, Set<ImmutablePair<Transition, Marking>>> cg,
                                                 ECNMarking initialMarking) {
        return null;
    }

}
