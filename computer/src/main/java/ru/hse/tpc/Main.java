package ru.hse.tpc;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.lang3.tuple.Pair;
import ru.hse.tpc.common.CyclicRun;
import ru.hse.tpc.common.Transition;
import ru.hse.tpc.desel.DeselAlgoSingleThreaded;
import ru.hse.tpc.priorities.PrioritiesComputationAlgo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws MalformedURLException {
        if (args.length < 2) {
            System.out.println("Usage: java -jar transition-priority-computer.jar petri_net_path output_dir_path");
        }
        String petriNetPath = args[0];
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(Pair.class, new IntPairDeserializer());
        objectMapper.registerModule(simpleModule);
        PetriNet petriNet;
        try (FileInputStream in = new FileInputStream(petriNetPath)) {
            petriNet = objectMapper.readValue(in, PetriNet.class);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error processing input Petri net", e);
        }

        List<CyclicRun> cyclicRuns = new DeselAlgoSingleThreaded().findCyclicRuns(petriNet.getTransitions(), petriNet.getMarking());
        if (cyclicRuns.isEmpty()) {
            System.out.println("Petri net has no cyclic runs. Terminating...");
            return;
        }
        // <DEBUG>
        System.out.println("Found cyclic runs:");
        cyclicRuns.forEach(System.out::println);
        // </DEBUG>
        Map<Transition, Integer> result = PrioritiesComputationAlgo.computePriorityValues(cyclicRuns,
                petriNet.getMarking(), petriNet.getTransitions());

        Path outputPath = Paths.get(args[1], "priority_values_" + System.currentTimeMillis() + ".json");
        System.out.println("Writing result to " + outputPath);
        try (FileOutputStream out = new FileOutputStream(outputPath.toString())) {
            objectMapper.writeValue(out, result);
        } catch (IOException e) {
            throw new RuntimeException("Error writing the result", e);
        }
    }

    private static class IntPairDeserializer extends StdDeserializer<Pair> {
        IntPairDeserializer() {
            this(null);
        }

        IntPairDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Pair deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException,
                JsonProcessingException {
            TreeNode treeNode = jsonParser.getCodec().readTree(jsonParser);
            if (!treeNode.isArray()) {
                throw new RuntimeException("Input json is of wrong format");
            }
            JsonNode node1 = ((ArrayNode) treeNode).get(0);
            JsonNode node2 = ((ArrayNode) treeNode).get(1);
            return Pair.of(node1.asInt(), node2.asInt());
        }
    }
}
