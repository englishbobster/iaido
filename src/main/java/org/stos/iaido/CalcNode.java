package org.stos.iaido;

import java.util.*;
import java.util.stream.Collectors;

public class CalcNode {

    private final UUID nodeId = UUID.randomUUID();
    private final double data;
    private double grad = 0;
    private final Set<CalcNode> children = new HashSet<>();
    private final Operation operation;
    private String label;

    public CalcNode(double data, String label) {
        this.data = data;
        this.operation = Operation.NO_OP;
        this.label = label;
    }

    private CalcNode(double data, List<CalcNode> children, Operation operation) {
        this.children.addAll(children);
        this.data = data;
        this.operation = operation;
        this.label = "";
    }

    public Set<CalcNode> getChildren() {
        return children;
    }

    public boolean hasChildren(){
        return !children.isEmpty();
    }

    public void setLabel(String label){
        this.label = label;
    }

    public void setGrad(double grad){
        this.grad = grad;
    }

    public double getGrad(){
        return grad;
    }

    public double getData(){
        return data;
    }

    public String getOperationSymbol(){
        return this.operation.symbol();
    }

    public Operation getOperation(){
        return this.operation;
    }

    public String getNodeId(){
        return nodeId.toString();
    }

    public CalcNode add(CalcNode other) {
        return new CalcNode(this.data + other.data, List.of(this, other), Operation.ADD);
    }

    public CalcNode multiply(CalcNode other) {
        return new CalcNode(this.data * other.data, List.of(this, other), Operation.MULTIPLY);
    }

    public CalcNode tanh(){
        double t = (Math.exp(2 * this.data) - 1) / (Math.exp(2 * this.data) + 1);
        return new CalcNode(t, List.of(this), Operation.TANH);
    }


    public List<CalcNode> toList(){
        Set<CalcNode> list = toList(new HashSet<>());
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
                + "\t\"grad\": " + grad + ",\n"
                + "\t\"label\": " + (label.isEmpty()? "\"\"" : "\"" + label + "\"") + ",\n"
                + "\t\"operation\": " +  "\""+ getOperationSymbol() +"\""  + ",\n"
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
        return Double.compare(calcNode.data, data) == 0 && calcNode.label.equals(label);
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(data);
        temp = (temp ^ (temp >>> 32));
        temp *= label.hashCode();
        return (int) temp;
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

    public enum Operation{
        ADD("+"), MULTIPLY("*"), TANH("tanh"), NO_OP("");

        private final String symbol;

        Operation(String symbol){
            this.symbol = symbol;
        }

        public String symbol() {
            return this.symbol;
        }
    }
}
