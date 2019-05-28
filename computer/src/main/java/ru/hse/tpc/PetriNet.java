package ru.hse.tpc;

import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;

import java.util.List;

public class PetriNet {

    private Marking marking;
    private List<Transition> transitions;

    public Marking getMarking() {
        return marking;
    }

    public void setMarking(Marking marking) {
        this.marking = marking;
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<Transition> transitions) {
        this.transitions = transitions;
    }
}
