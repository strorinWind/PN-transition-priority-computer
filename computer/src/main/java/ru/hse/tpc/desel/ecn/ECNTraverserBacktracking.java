package ru.hse.tpc.desel.ecn;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;

import java.util.*;
import java.util.stream.Collectors;

import static ru.hse.tpc.desel.DeselAlgo.UNLIMITED;

public class ECNTraverserBacktracking extends ECNTraverserImpl {

    @Override
    public List<CyclicRun> findCyclicRuns(Map<Marking, List<ImmutablePair<Transition, Marking>>> cg,
                                          ECNMarking initialMarking, Set<Transition> transitionSet) {
        Map<Marking, Entry> graph = cg.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new Entry(e.getValue())));
        return findCyclicRuns(graph, initialMarking, transitionSet, UNLIMITED);
    }

    public List<CyclicRun> findCyclicRuns(Map<Marking, Entry> cg, ECNMarking initialMarking, Set<Transition> transitionSet,
                                          int limit) {
        List<CyclicRun> cyclicRuns = new ArrayList<>();
        TraverseNode curNode = new TraverseNode(initialMarking);
        boolean isBacktracked = false;
        while (curNode != null) {
            if (containsCyclicRun(curNode)) {
                CyclicRun cyclicRun = checkForCyclicRun(curNode).orElseThrow(RuntimeException::new);
                if (cyclicRunContainsAllTransitions(transitionSet, cyclicRun)) {
                    cyclicRuns.add(cyclicRun);
                    if (limit != UNLIMITED && cyclicRuns.size() >= limit) {
                        break;
                    }
                    curNode = curNode.getParent();
                    isBacktracked = true;
                    continue;
                }
            }
            // if cycle is not detected
            Entry e = cg.get(curNode.getMarking().getOriginalPlace());
            if (!isBacktracked) {
                e.incPhase();
            }
            Set<Transition> transitionsNotInPath =
                    removeOccurredTransitions(curNode, cg.get(curNode.getMarking().getOriginalPlace()).outEdges).stream()
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

    private List<ImmutablePair<Transition, Marking>> removeOccurredTransitions(TraverseNode baseMarkingNode,
                                                                                 List<Entry.Edge> outgoingEdges) {
        Map<Transition, Marking> tToResultingM = outgoingEdges.stream()
                .collect(Collectors.toMap(Entry.Edge::getT, Entry.Edge::getM));
        Marking baseMarking = baseMarkingNode.getMarking().getOriginalPlace();
        TraverseNode curNode = baseMarkingNode.getParent();
        Transition curT = baseMarkingNode.getIncT();
        while (curNode != null) {
            if (curNode.getMarking().getOriginalPlace().equals(baseMarking)) {
                tToResultingM.remove(curT);
            }
            curT = curNode.getIncT();
            curNode = curNode.getParent();
        }
        return tToResultingM.entrySet().stream().map(e -> ImmutablePair.of(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public static class Entry {
        private int vertexPhase;
        private List<Edge> outEdges;

        public Entry(List<ImmutablePair<Transition, Marking>> edges) {
            this.vertexPhase = 0;
            this.outEdges = edges.stream().map(Edge::new).collect(Collectors.toList());
        }

        public int numEdges() {
            return outEdges.size();
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

            Transition getT() {
                return t;
            }

            Marking getM() {
                return m;
            }
        }
    }

}
