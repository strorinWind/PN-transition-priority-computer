package ru.hse.tpc.cg;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.domain.Marking;
import ru.hse.tpc.domain.Transition;

import java.util.Map;
import java.util.Set;

public interface CGBuilder {

    Map<Marking, Set<ImmutablePair<Transition, Marking>>> build(Marking initialMarking);

}
