package org.stos.iaido;

import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Rank.RankDir;
import guru.nidi.graphviz.attribute.Records;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Factory;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.MutableNode;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Grapher {

    public static void draw(CalcNode root) {
        List<CalcNode> calcNodes = root.toList();

        Graph graph = Factory.graph("operation chain").directed()
                .graphAttr().with(Rank.dir(RankDir.LEFT_TO_RIGHT))
                .nodeAttr().with(Shape.RECTANGLE).linkAttr().with("class", "link-class");

        Map<UUID, MutableNode> nodeByUuid = calcNodes.stream()
                .collect(Collectors.toMap(CalcNode::getNodeId, calcNode -> Factory.mutNode(calcNode.getLabel())
                        .add(Records.of(Records.turn(Records.rec(calcNode.getLabel()),
                                Records.rec("data:" + String.format(Locale.ENGLISH, "%.3f", calcNode.getData())),
                                Records.rec("grad:" + String.format(Locale.ENGLISH, "%.3f", calcNode.getGrad())))))));


        for(CalcNode calcNode: calcNodes){
            if(calcNode.hasChildren()){
                for(UUID child: calcNode.getChildren().stream().map(CalcNode::getNodeId).toList()){
                    if(!calcNode.getOperationSymbol().isEmpty()){
                        MutableNode opNode = Factory.mutNode(calcNode.toString())
                                .add(Records.label(calcNode.getOperationSymbol()))
                                .add(Shape.CIRCLE).addLink(nodeByUuid.get(calcNode.getNodeId()));
                        nodeByUuid.put(UUID.randomUUID(), opNode);
                        System.out.println(child);
                        nodeByUuid.get(child).addLink(opNode);
                    } else {
                        nodeByUuid.get(child).addLink(nodeByUuid.get(calcNode.getNodeId()));
                    }
                }
            }
        }
        Graph withNodes = graph.with(nodeByUuid.values().stream().toList());
        try {
            Graphviz.fromGraph(withNodes).render(Format.PNG).toFile(new File("./output/graph.png"));
        } catch (IOException e) {
            System.out.println("Error while rendering graph");
            throw new RuntimeException(e);
        }
    }


}