package ru.hse.tpc.gui.graph

import org.graphstream.graph.Graph
import org.graphstream.graph.implementations.MultiGraph
import ru.hse.tpc.PetriNet

class GraphDisplay(
        private val petriNet: PetriNet
) {

    fun createGraphFromNet(): Graph {
        val graph = MultiGraph("Clicks")

        var c = 0
        for (i in petriNet.marking.iterator()) {
            graph.addNode(c.toString())
            c++
        }
        c = 0
        for (i in petriNet.transitions.iterator()) {
            val name = "t$c"
            graph.addNode(name)
            val node = graph.getNode(name)
            node.setAttribute("ui.style", "shape: box; size: 30px, 30px;")
            for (j in i.preList) {
                //TODO: mark weight
                graph.addEdge("a$j-$name", graph.getNode(j.left.toString()), node, true)
            }
            for (j in i.occurrenceResult) {
                //TODO: check for extra in preList
                // TODO: mark weight
                val preList = i.getPreList(j.key)?.right ?: 0
                if (j.value != -preList) {
                    val jNode = graph.getNode(j.key.toString())
                    if (j.value >= 0) {
                        graph.addEdge("a$name-$j", node, jNode, true)
                    } else {
                        graph.addEdge("a$j-$name", jNode, node, true)
                    }
                }
            }
            c++
        }
//        graph.getNode("A").setAttribute("ui.style", "shape: box; size: 30px, 30px;")
//                graph.getNode("A").setAttribute("ui.style", "fill-color: rgb(0,100,255);");
        return graph
    }
}