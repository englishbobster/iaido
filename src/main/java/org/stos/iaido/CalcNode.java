package org.stos.iaido;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * backpropagation rules:
 * Back propagation is basically the chain rule in action:
 * To distribute gradiant values backwards through the graph follow these rules:
 * Root node grad is 1.00
 * multiplication parent: current child node grad = parent node grad * sibling node data
 * addition parent: current node grad = parent node (addition distributes parent grad)
 * <p>
 * <p>
 * nudge leaf node data (available inputs) by small amount and recalculate data
 * node data += (small amount * node grad)
 * <p>
 * recalculate all node gradients according to the rules.
 **/

public class CalcNode {

    private final UUID nodeId = UUID.randomUUID();
    private final double data;
    private double grad = 0;
    private final Set<CalcNode> children = new HashSet<>();
    private final Operation operation;
    private String label;
    private Consumer<CalcNode> differential;

    public CalcNode(double data, String label) {
        this.data = data;
        this.operation = Operation.NO_OP;
        this.label = label;
        this.differential = cn -> {};
    }

    private CalcNode(double data, List<CalcNode> children, Operation operation) {
        this.differential = cn -> {};
        this.children.addAll(children);
        this.data = data;
        this.operation = operation;
        this.label = "";
    }

    public void backPropagate() {
        this.setGrad(1.00);
        updateChildren(this);
    }

    private void updateChildren(CalcNode root) {
        if(root.hasChildren()) {
            root.localDerivative();
            root.getChildren().forEach(this::updateChildren);
        }
    }

    public void localDerivative(){
        this.differential.accept(this);
    }

    private void setDifferential(Consumer<CalcNode> differential){
        this.differential = differential;
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
        CalcNode out = new CalcNode(this.data + other.data, List.of(this, other), Operation.ADD);
        Consumer<CalcNode> backProp = cn -> {
            this.grad += out.getGrad();
            other.grad += out.getGrad();
        };
        out.setDifferential(backProp);
        return out;
    }

    public CalcNode add(Integer other) {
        CalcNode constant = new CalcNode(other.doubleValue(), "CONST_" + other);
        CalcNode out = new CalcNode(this.data + constant.data, List.of(this, constant), Operation.ADD);
        Consumer<CalcNode> backProp = cn -> {
            this.grad += out.getGrad();
            constant.grad = out.getGrad();
        };
        out.setDifferential(backProp);
        return out;
    }

    public CalcNode multiply(CalcNode other) {
        CalcNode out = new CalcNode(this.data * other.data, List.of(this, other), Operation.MULTIPLY);
        Consumer<CalcNode> backProp = cn -> {
            this.grad += (out.getGrad() * other.getData());
            other.grad += (out.getGrad() * this.getData());
        };
        out.setDifferential(backProp);
        return out;
    }

    public CalcNode multiply(Integer other) {
        CalcNode constant = new CalcNode(other.doubleValue(), "CONST_" + other);
        CalcNode out = new CalcNode(this.data * constant.data, List.of(this, constant), Operation.MULTIPLY);
        Consumer<CalcNode> backProp = cn -> {
            this.grad += (out.getGrad() * constant.getData());
            constant.grad += (out.getGrad() * this.getData());
        };
        out.setDifferential(backProp);
        return out;
    }

    public CalcNode tanh(){
        double t = (Math.exp(2 * this.data) - 1) / (Math.exp(2 * this.data) + 1);
        CalcNode out = new CalcNode(t, List.of(this), Operation.TANH);
        Consumer<CalcNode> backProp = cn -> this.grad += ((1 - (t * t)) * out.getGrad());
        out.setDifferential(backProp);
        return out;
    }

    public CalcNode exp(){
        CalcNode out = new CalcNode(Math.exp(this.data), List.of(this), Operation.EXP);
        Consumer<CalcNode> backProp = cn -> this.grad += out.getData() * out.getGrad();
        out.setDifferential(backProp);
        return out;
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

    public enum Operation{
        ADD("+"), MULTIPLY("*"), TANH("tanh"), NO_OP(""), EXP("e");

        private final String symbol;

        Operation(String symbol){
            this.symbol = symbol;
        }

        public String symbol() {
            return this.symbol;
        }
    }
}
