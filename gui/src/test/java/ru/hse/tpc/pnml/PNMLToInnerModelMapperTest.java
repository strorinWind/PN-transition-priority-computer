package ru.hse.tpc.pnml;

import io.vavr.control.Either;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import ru.hse.tpc.PetriNet;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class PNMLToInnerModelMapperTest {

    private Set<Transition> transitionSet;
    private Marking marking;

    @Test
    public void mapTest() throws URISyntaxException {
        URL url = this.getClass().getClassLoader().getResource("pnml/producer_consumer.pnml");
        File file = new File(url.toURI());
        Either<String, PetriNet> res = new PNMLToInnerModelMapper().map(file);
        assertTrue(res.isRight());
        assertEquals(marking, res.get().getMarking());
        assertEquals(transitionSet.size(), res.get().getTransitions().size());
        assertTrue(transitionSet.containsAll(res.get().getTransitions()));
    }

    @Before
    public void setup() {
        Transition a = new Transition("a",
                Collections.singletonList(ImmutablePair.of(1, 1)),
                Collections.singletonList(ImmutablePair.of(0, 1)));
        Transition b = new Transition("b",
                Collections.singletonList(ImmutablePair.of(0, 1)),
                Arrays.asList(
                        ImmutablePair.of(1, 1),
                        ImmutablePair.of(2, 1)
                ));
        Transition c = new Transition("c",
                Arrays.asList(
                        ImmutablePair.of(2, 2),
                        ImmutablePair.of(3, 1)
                ),
                Collections.singletonList(ImmutablePair.of(4, 1)));
        Transition d = new Transition("d",
                Collections.singletonList(ImmutablePair.of(4, 1)),
                Collections.singletonList(ImmutablePair.of(3, 1)));

        transitionSet = new HashSet<>(Arrays.asList(a,b,c,d));
        marking = new Marking(1,0,0,1,0);
    }
}