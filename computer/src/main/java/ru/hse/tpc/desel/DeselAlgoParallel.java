package ru.hse.tpc.desel;

import com.google.common.flogger.FluentLogger;
import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;
import ru.hse.tpc.desel.cg.CGBuilder;
import ru.hse.tpc.desel.cg.CGBuilderParallel;
import ru.hse.tpc.desel.ecn.ECNMarking;
import ru.hse.tpc.desel.ecn.ECNTraverserBacktracking;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.hse.tpc.desel.ecn.ECNTraverserBacktracking.Entry;
import static com.google.common.flogger.LazyArgs.lazy;

/**
 * Implementation of the Desel algorithm for the search of feasible cycles with the parallel
 * coverability graph construction and the backtracking-based search.
 */
public class DeselAlgoParallel extends AbstractDeselAlgo {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private final CGBuilder cgBuilder;
    private final ECNTraverserBacktracking ecnTraverser;

    public DeselAlgoParallel(ExecutorService executorService) {
        this.cgBuilder = new CGBuilderParallel(executorService);
        this.ecnTraverser = new ECNTraverserBacktracking();
    }

    @Override
    public List<CyclicRun> findCyclicRuns(List<Transition> transitions, Marking initialMarking) {
        return findCyclicRuns(transitions, initialMarking, ECNTraverserBacktracking.UNLIMITED);
    }

    @Override
    public List<CyclicRun> findCyclicRuns(List<Transition> transitions, Marking initialMarking, int limit) {
        Map<Marking, Entry> cg = cgBuilder.build(initialMarking, transitions).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new Entry(e.getValue())));

        logger.atInfo().log("Coverability graph was successfully built; Number of vertices: %d; Number of edges: %d",
                cg.size(), lazy(() -> cg.values().stream().mapToInt(Entry::numEdges).sum()));

        Set<Integer> unboundedPlaces = findUnboundedPlaces(cg.keySet());

        if (unboundedPlaces.isEmpty()) {
            logger.atInfo().log("No unbounded places in the net");
        }

        Map<Integer, Integer> additionalPlacesMarking = unboundedPlaces.stream()
                .collect(Collectors.toMap(Function.identity(), initialMarking::getMarking));

        return ecnTraverser.findCyclicRuns(cg, new ECNMarking(initialMarking, additionalPlacesMarking),
                new HashSet<>(transitions), limit);
    }
}
