package ru.hse.tpc.pnml;

import fr.lip6.move.pnml.framework.hlapi.HLAPIRootClass;
import fr.lip6.move.pnml.framework.utils.PNMLUtils;
import fr.lip6.move.pnml.framework.utils.exception.ImportException;
import fr.lip6.move.pnml.framework.utils.exception.InvalidIDException;
import fr.lip6.move.pnml.ptnet.hlapi.*;
import io.vavr.control.Either;
import org.apache.commons.lang3.tuple.Pair;
import ru.hse.tpc.PetriNet;
import ru.hse.tpc.common.Marking;
import ru.hse.tpc.common.Transition;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class PNMLToInnerModelMapper {

    public Either<String, PetriNet> map(File pnmlFile) {
        try {
            HLAPIRootClass rc = PNMLUtils.importPnmlDocument(pnmlFile, false);
            PetriNetDocHLAPI doc = (PetriNetDocHLAPI) rc;
            PetriNetHLAPI petriNetHLAPI = doc.getNetsHLAPI().get(0);
            PageHLAPI pageHLAPI = petriNetHLAPI.getPagesHLAPI().get(0);
            return Either.right(map(pageHLAPI));
        } catch (ImportException | InvalidIDException e) {
            return Either.left("Failed to import from file: " + pnmlFile.getName() + " " + e.getMessage());
        } catch (ClassCastException e) {
            return Either.left("Unsupported Petri net type");
        } catch (Exception e) {
            return Either.left("Error during processing pnml: " + e.toString() + Arrays.toString(e.getStackTrace()));
        }
    }

    private PetriNet map(PageHLAPI page) {
        List<Transition> transitions = getTransitions(page);
        Marking marking = getMarking(page.getObjects_PlaceHLAPI());
        PetriNet pn = new PetriNet();
        pn.setTransitions(transitions);
        pn.setMarking(marking);
        return pn;
    }

    private List<Transition> getTransitions(PageHLAPI page) {
        List<PlaceHLAPI> places = page.getObjects_PlaceHLAPI();
        Map<String, Integer> placeIndex = new HashMap<>(places.size());
        for (int i = 0; i < places.size(); i++) {
            placeIndex.put(places.get(i).getId(), i);
        }
        List<TransitionHLAPI> transitions = page.getObjects_TransitionHLAPI();
        Set<String> tIdSet = transitions.stream().map(TransitionHLAPI::getId).collect(Collectors.toSet());
        Map<String, List<Pair<Integer, Integer>>> tIdToPre = new HashMap<>();
        Map<String, List<Pair<Integer, Integer>>> tIdToPost = new HashMap<>();
        List<ArcHLAPI> arcs = page.getObjects_ArcHLAPI();
        for (ArcHLAPI arc : arcs) {
            Integer arcWeight = Optional.ofNullable(arc.getInscriptionHLAPI())
                    .flatMap(i -> Optional.ofNullable(i.getText()))
                    .map(Math::toIntExact)
                    .orElse(1);
            if (tIdSet.contains(arc.getSourceHLAPI().getId())) {
                tIdToPost.computeIfAbsent(arc.getSourceHLAPI().getId(), k -> new ArrayList<>())
                        .add(Pair.of(
                                placeIndex.get(arc.getTargetHLAPI().getId()),
                                arcWeight
                        ));
            } else {
                tIdToPre.computeIfAbsent(arc.getTargetHLAPI().getId(), k -> new ArrayList<>())
                        .add(Pair.of(
                                placeIndex.get(arc.getSourceHLAPI().getId()),
                                arcWeight
                        ));
            }
        }
        return transitions.stream()
                .map(t -> new Transition(t.getName().getText(), tIdToPre.get(t.getId()), tIdToPost.get(t.getId())))
                .collect(Collectors.toList());
    }

    private Marking getMarking(List<PlaceHLAPI> places) {
        int[] marking = new int[places.size()];
        for (int i = 0; i < places.size(); i++) {
            marking[i] = Optional.ofNullable(places.get(i).getInitialMarkingHLAPI())
                    .flatMap(m -> Optional.ofNullable(m.getText()))
                    .map(Math::toIntExact)
                    .orElse(0);
        }
        return new Marking(marking);
    }
}
