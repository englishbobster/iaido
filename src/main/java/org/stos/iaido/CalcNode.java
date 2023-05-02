package org.stos.iaido;

import java.util.*;
import java.util.stream.Collectors;

public class CalcNode {

    private final UUID nodeId = UUID.randomUUID();
    private final double data;
    private final Set<CalcNode> children = new HashSet<>();
    private final Operation operation;
    private  String label;

    public CalcNode(double data, Pair children, Operation operation, String label) {
        this.children.add(children.A);
        this.children.add(children.B);
        this.data = data;
        this.operation = operation;
        this.label = label;
    }

    public CalcNode(double data, Pair children, Operation operation) {
        this.children.add(children.A);
        this.children.add(children.B);
        this.data = data;
        this.operation = operation;
        this.label = "";
    }

    public CalcNode(double data, String label) {
        this.data = data;
        this.operation = Operation.NO_OP;
        this.label = label;
    }

    public Set<CalcNode> getChildren() {
        return children;
    }

    public String getLabel(){
        return label;
    }

    public void setLabel(String label){
        this.label = label;
    }
    public String getOperation(){
        return this.operation.symbol();
    }

    public String getNodeId(){
        return nodeId.toString();
    }

    public CalcNode add(CalcNode other) {
        return new CalcNode(this.data + other.data, Pair.of(this, other), Operation.ADD);
    }

    public CalcNode multiply(CalcNode other) {
        return new CalcNode(this.data * other.data, Pair.of(this, other), Operation.MULTIPLY);
    }


    public List<CalcNode> toList(){
        Set<CalcNode> list = toList(new HashSet<CalcNode>());
        return list.stream().toList();
    }

    private Set<CalcNode> toList(Set<CalcNode> nodes){
        Set<CalcNode> kids = this.getChildren();
        nodes.add(this);
        for(CalcNode kid: kids){
            if(kid.getChildren().isEmpty()) {
                nodes.add(kid);
            } else {
                kid.toList(nodes);
            }
        }
        return nodes;
    }

    @Override
    public String toString() {
        return "{\n"
                + "\t\"nodeId\": \"" + nodeId + "\",\n"
                + "\t\"data\": " + data + ",\n"
                + "\t\"label\": " + (label.isEmpty()? "\"\"" : "\"" + label + "\"") + ",\n"
                + "\t\"operation\": " +  "\""+ getOperation() +"\""  + ",\n"
                + "\t\"children\": "
                + (children.isEmpty() ? "[]" : children.stream().map(child -> "\"" + child.nodeId + "\"")
                .collect(Collectors.joining(", ","[\n","\n]")))
                +"\n}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalcNode calcNode = (CalcNode) o;
        return Double.compare(calcNode.data, data) == 0;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(data);
        return (int) (temp ^ (temp >>> 32));
    }

    private record Pair(CalcNode A, CalcNode B){
        static Pair of(CalcNode A, CalcNode B){
            return new Pair(A, B);
        }

        @Override
        public String toString(){
            return A.getNodeId() + "," + B.getNodeId();
        }
    }

    private enum Operation{
        ADD("+"), MULTIPLY("*"), NO_OP("");

        private final String symbol;

        Operation(String symbol){
            this.symbol = symbol;
        }

        public String symbol() {
            return this.symbol;
        }
    }

}
