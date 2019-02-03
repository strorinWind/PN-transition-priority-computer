package ru.hse.tpc.cg;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.hse.tpc.common.StatusMessage;
import ru.hse.tpc.domain.CGVertex;
import ru.hse.tpc.domain.Marking;
import ru.hse.tpc.domain.Transition;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;

public class CGBuilder {

    private final ExecutorService executorService;
    private final int numWorkers;
    private final BlockingQueue<Optional<ImmutablePair<CGVertex, Transition>>> workUnitPool;
    private final BlockingQueue<StatusMessage> managerMsgBuffer;

    private final List<Transition> transitions;
    private final ConcurrentMap<Marking, Set<ImmutablePair<String, Marking>>> graph;

    public CGBuilder(ExecutorService executorService, int numWorkers, List<Transition> transitions) {
        this.executorService = executorService;
        this.numWorkers = numWorkers;
        this.workUnitPool = new LinkedBlockingQueue<>();
        this.managerMsgBuffer = new LinkedBlockingQueue<>();

        this.transitions = transitions;
        this.graph = new ConcurrentHashMap<>();
    }

    public Map<Marking, Set<ImmutablePair<String, Marking>>> build(Marking initialMarking) {
        return null;
    }

    private Runnable getTaskForWorker() {
        return () -> {
          while (true) {
              Optional<ImmutablePair<CGVertex, Transition>> workUnitO = Optional.empty();
              try {
                  workUnitO = workUnitPool.take();
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
              if (!workUnitO.isPresent()) {
                  System.out.println("Worker id " + Thread.currentThread().getId() + " terminates.");
                  break;
              }
              managerMsgBuffer.add(StatusMessage.BUSY);
              // <logic>
              CGVertex v = workUnitO.get().left;
              Transition t = workUnitO.get().right;
              CGVertex newV = t.fire(v);
              graph.get(v.getM()).add(ImmutablePair.of(t.getLabel(), newV.getM()));
              // </logic>
          }
        };
    }
}
