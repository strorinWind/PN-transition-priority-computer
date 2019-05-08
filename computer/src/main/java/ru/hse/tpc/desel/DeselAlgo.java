package ru.hse.tpc.desel;

import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;

import java.util.List;

public interface DeselAlgo {

    List<CyclicRun> findCyclicRuns(List<Transition> transitions, Marking initialMarking);

}
