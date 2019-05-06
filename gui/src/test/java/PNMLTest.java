import fr.lip6.move.pnml.framework.general.PNType;
import fr.lip6.move.pnml.framework.hlapi.HLAPIRootClass;
import fr.lip6.move.pnml.framework.utils.PNMLUtils;
import fr.lip6.move.pnml.framework.utils.exception.ImportException;
import fr.lip6.move.pnml.framework.utils.exception.InvalidIDException;
import fr.lip6.move.pnml.ptnet.hlapi.PageHLAPI;
import fr.lip6.move.pnml.ptnet.hlapi.PetriNetDocHLAPI;
import fr.lip6.move.pnml.ptnet.hlapi.PetriNetHLAPI;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PNMLTest {

    @Test
    public void test() throws URISyntaxException, InvalidIDException, ImportException {
        URL url = this.getClass().getClassLoader().getResource("pnml/ClientsAndServers.pnml");
        assertNotNull(url);
        File file = new File(url.toURI());
        HLAPIRootClass rc = PNMLUtils.importPnmlDocument(file, false);
        assertEquals(PNType.PTNET, PNMLUtils.determinePNType(rc));
        PetriNetDocHLAPI doc = (PetriNetDocHLAPI) rc;
        PetriNetHLAPI petriNetHLAPI = doc.getNetsHLAPI().get(0);
        System.out.println(petriNetHLAPI.getName());
        PageHLAPI pageHLAPI = petriNetHLAPI.getPagesHLAPI().get(0);
        System.out.println("Num places: " + pageHLAPI.getObjects_PlaceHLAPI().size());
        System.out.println("Num transitions: " + pageHLAPI.getObjects_TransitionHLAPI().size());
        System.out.println("Num arcs: " + pageHLAPI.getObjects_ArcHLAPI().size());
    }

}
