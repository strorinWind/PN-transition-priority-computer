package ru.hse.tpc.desel.ecn;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.desel.domain.CyclicRun;
import ru.hse.tpc.desel.domain.Marking;
import ru.hse.tpc.desel.domain.Transition;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ECNTraverser {

    List<CyclicRun> findCyclicRuns(Map<Marking, Set<ImmutablePair<Transition, Marking>>> cg, ECNMarking initialMarking, Set<Transition> transitionSet);

}
