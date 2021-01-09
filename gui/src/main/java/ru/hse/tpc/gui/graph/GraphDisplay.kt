package ru.hse.tpc.gui.graph

import org.graphstream.graph.Graph
import org.graphstream.graph.implementations.MultiGraph
import ru.hse.tpc.PetriNet

class GraphDisplay(
        private val petriNet: PetriNet
) {

    fun createGraphFromNet(): Graph {
        val graph = MultiGraph("Clicks")
        graph.addNode("A")
        graph.addNode("B")
        graph.addNode("C")
        graph.getNode("A").setAttribute("ui.style", "shape: box; size: 30px, 30px;")
        //        graph.getNode("A").setAttribute("ui.style", "fill-color: rgb(0,100,255);");
        graph.addEdge("AB", "A", "B")
        graph.addEdge("BC", "B", "C")
        graph.addEdge("CA", "C", "A")
        return graph
    }
}