package ru.hse.tpc.priorities;

import ru.hse.tpc.desel.domain.CyclicRun;
import ru.hse.tpc.desel.domain.Marking;
import ru.hse.tpc.priorities.domain.SpineTreeNode;

import java.util.List;

public interface SpineTreeBuilder {

    SpineTreeNode build(Marking initialMarking, List<CyclicRun> cyclicRuns);

}
