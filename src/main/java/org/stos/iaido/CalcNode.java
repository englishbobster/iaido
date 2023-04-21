package org.stos.iaido;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CalcNode {

    private final UUID nodeId = UUID.randomUUID();
    private final double data;
    private final Set<CalcNode> children = new HashSet<>();
    private final Operation operation;

    public CalcNode(double data, Pair children, Operation operation) {
        this.children.add(children.A);
        this.children.add(children.B);
        this.data = data;
        this.operation = operation;
    }

    public CalcNode(double data) {
        this.data = data;
        this.operation = Operation.NO_OP;
    }

    public Set<CalcNode> getChildren() {
        return children;
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

    @Override
    public String toString() {
        return "{\n"+ "\"calcNode\": {\n"
                + "\t\"nodeId\": " + nodeId + ",\n"
                + "\t\"data\": " + data + ",\n"
                + "\t\"children\": "
                + (children.isEmpty() ? "" : children.stream().map(child -> child.nodeId.toString()).collect(Collectors.joining(", ","[","]")))
                +"\n}}";
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
