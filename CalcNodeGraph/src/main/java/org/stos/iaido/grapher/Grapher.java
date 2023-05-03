package org.stos.iaido.grapher;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Records;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.MutableNode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.attribute.Records.rec;
import static guru.nidi.graphviz.attribute.Records.turn;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.mutNode;

public class Grapher {

    public static void main(String[] args) throws IOException {
        List<CalcNode> calcNodes = readJson();

        Graph graph = graph("operation chain").directed()
                .graphAttr().with(Rank.dir(LEFT_TO_RIGHT))
                .nodeAttr().with(Shape.RECTANGLE).linkAttr().with("class", "link-class");

        Map<UUID, MutableNode> nodeByUuid = calcNodes.stream()
                .collect(Collectors.toMap(CalcNode::nodeId, calcNode -> mutNode(calcNode.label())
                        .add(Records.of(turn(rec(calcNode.label()),
                                rec("data:" + calcNode.data()),
                                rec("grad:" + calcNode.grad())
                        )))));

        for(CalcNode calcNode: calcNodes){
            if(calcNode.hasChildren()){
                for(UUID child: calcNode.children().stream().map(UUID::fromString).toList()){
                    if(calcNode.hasOperation()){
                        MutableNode opNode = mutNode(calcNode.operation()).add(Shape.CIRCLE).addLink(nodeByUuid.get(calcNode.nodeId()));
                        nodeByUuid.put(UUID.randomUUID(), opNode);
                        nodeByUuid.get(child).addLink(opNode);
                    } else {
                        nodeByUuid.get(child).addLink(nodeByUuid.get(calcNode.nodeId()));
                    }
                }
            }
        }
        Graph withNodes = graph.with(nodeByUuid.values().stream().toList());
        Graphviz.fromGraph(withNodes).render(Format.PNG).toFile(new File("./output/graph.png"));
    }


    private static List<CalcNode> readJson() throws IOException {
        byte[] bytes = Files.readAllBytes(Path.of("./output/nodes.json"));
        return Arrays.stream(new ObjectMapper().readValue(new String(bytes, StandardCharsets.UTF_8), CalcNode[].class))
                .toList();
    }

    public record CalcNode(UUID nodeId, double data, double grad, List<String> children, String operation, String label){
        public boolean hasChildren(){
            return !children.isEmpty();
        }

        public boolean hasOperation(){
            return !operation.isEmpty();
        }
    }

}
