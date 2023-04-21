package org.stos.iaido;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CalcNode {

    private final double data;
    private final Set<CalcNode> children = new HashSet<>();

    public CalcNode(double data, Pair children) {
        this.children.add(children.A);
        this.children.add(children.B);
        this.data = data;
    }

    public CalcNode(double data) {
        this.data = data;
    }

    public Set<CalcNode> getChildren() {
        return children;
    }

    public CalcNode add(CalcNode other) {
        return new CalcNode(this.data + other.data, Pair.of(this, other));
    }

    public CalcNode multiply(CalcNode other) {
        return new CalcNode(this.data * other.data, Pair.of(this, other));
    }

    @Override
    public String toString() {
        return "{\"calcNode\": {\n\"data\": " + data + ",\n\"children\": ["
                + children.stream().map(calcNode -> calcNode.getClass().getName()).collect(Collectors.joining(","))
                + "]}}";
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
    }

}
