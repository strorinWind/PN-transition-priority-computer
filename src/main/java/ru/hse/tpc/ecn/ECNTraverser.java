package ru.hse.tpc.ecn;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.domain.Marking;
import ru.hse.tpc.domain.Transition;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ECNTraverser {

    List<List<Transition>> findCyclicRuns(Map<Marking, Set<ImmutablePair<Transition, Marking>>> cg, ECNMarking initialMarking);

}
