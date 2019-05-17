package ru.hse.tpc.desel.ecn;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;

import java.util.*;
import java.util.stream.Collectors;

public class ECNTraverserBacktracking extends ECNTraverserImpl {

    private static final int CYCLIC_RUNS_LIMIT = 20;

    @Override
    public List<CyclicRun> findCyclicRuns(Map<Marking, List<ImmutablePair<Transition, Marking>>> cg,
                                          ECNMarking initialMarking, Set<Transition> transitionSet) {
        List<CyclicRun> cyclicRuns = new ArrayList<>();
        Map<Marking, Entry> graph = cg.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new Entry(e.getValue())));
        TraverseNode curNode = new TraverseNode(initialMarking);
        boolean isBacktracked = false;
        int nodesProcessed = 0;
        while (curNode != null) {
            System.out.println("Path size: " + curNode.getPathSize() + " Nodes processed: " + nodesProcessed +
                    " Cyclic runs found: " + cyclicRuns.size());
            if (containsCyclicRun(curNode)) {
                CyclicRun cyclicRun = checkForCyclicRun(curNode).orElseThrow(RuntimeException::new);
                if (cyclicRunContainsAllTransitions(transitionSet, cyclicRun)) {
                    cyclicRuns.add(cyclicRun);
                    if (cyclicRuns.size() >= CYCLIC_RUNS_LIMIT) {
                        break;
                    }
                    curNode = curNode.getParent();
                    isBacktracked = true;
                    continue;
                }
            }
            // if cycle is not detected
            Entry e = graph.get(curNode.getMarking().getOriginalPlace());
            if (!isBacktracked) {
                e.incPhase();
            }
            Set<Transition> transitionsNotInPath =
                    filterOutOccurredTransitions(curNode, cg.get(curNode.getMarking().getOriginalPlace())).stream()
                            .map(ImmutablePair::getLeft).collect(Collectors.toSet());
            final TraverseNode parentNode = curNode;
            Optional<Pair<Entry.Edge, Optional<TraverseNode>>> nextO = e.outEdges.stream()
                    .filter(edge -> transitionsNotInPath.contains(edge.t))
                    .filter(edge -> edge.isNotVisitedInPhase(e.vertexPhase))
                    .map(edge -> Pair.of(edge, toNewTraverseNode(parentNode).apply(ImmutablePair.of(edge.t, edge.m))))
                    .filter(p -> p.getRight().isPresent())
                    .findFirst();
            if (nextO.isPresent()) {
                Pair<Entry.Edge, Optional<TraverseNode>> next = nextO.get();
                next.getLeft().visitedInPhase(e.vertexPhase);
                curNode = next.getRight().get();
                isBacktracked = false;
            } else {
                e.outEdges.forEach(edge -> edge.backtrackPhase(e.vertexPhase));
                e.decPhase();
                curNode = curNode.getParent();
                isBacktracked = true;
            }
            nodesProcessed++;
        }

        return cyclicRuns;
    }

    private boolean containsCyclicRun(TraverseNode node) {
        ECNMarking marking = node.getMarking();
        TraverseNode curNode = node.getParent();
        while (curNode != null) {
            if (marking.equals(curNode.getMarking())) {
                return true;
            }
            curNode = curNode.getParent();
        }
        return false;
    }

    private static class Entry {
        int vertexPhase;
        List<Edge> outEdges;

        Entry(List<ImmutablePair<Transition, Marking>> edges) {
            this.vertexPhase = 0;
            this.outEdges = edges.stream().map(Edge::new).collect(Collectors.toList());
        }

        void incPhase() {
            vertexPhase++;
        }

        void decPhase() {
            vertexPhase--;
        }

        private static class Edge {
            Deque<Integer> phaseHistory;
            Transition t;
            Marking m;

            Edge(ImmutablePair<Transition, Marking> p) {
                this.phaseHistory = new LinkedList<>();
                this.t = p.left;
                this.m = p.right;
            }

            boolean isNotVisitedInPhase(int phase) {
                return phaseHistory.isEmpty() || phaseHistory.peek() != phase;
            }

            void visitedInPhase(int phase) {
                phaseHistory.push(phase);
            }

            void backtrackPhase(int phase) {
                if (!phaseHistory.isEmpty() && phaseHistory.peek() == phase) {
                    phaseHistory.pop();
                }
            }
        }
    }

}
