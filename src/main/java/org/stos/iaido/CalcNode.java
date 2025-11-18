package org.stos.iaido;

import java.util.*;
import java.util.function.Consumer;

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
    private final double value;
    private double grad = 0;
    private final Set<CalcNode> children = new HashSet<>();
    private String operation = "";
    private String label = "";
    private Consumer<CalcNode> differential = calcNode -> {
    };

    public CalcNode(double value, String label) {
        this.value = value;
        this.label = label;
    }

    private CalcNode(double value, List<CalcNode> children, String operation) {
        this.children.addAll(children);
        this.value = value;
        this.operation = operation;
    }

    public void backPropagate() {
        List<CalcNode> actualTopo = new ArrayList<>();
        Set<CalcNode> visited = new HashSet<>();
        buildTopo(this, actualTopo, visited);

        this.grad = 1.00;
        actualTopo.reversed().forEach(CalcNode::localDerivative);
    }

    private void buildTopo(CalcNode root, List<CalcNode> actualTopo, Set<CalcNode> visited) {
        if (!visited.contains(root)) {
            visited.add(root);
            for (CalcNode child : root.children) {
                buildTopo(child, actualTopo, visited);
            }
            actualTopo.add(root);
        }
    }

    public void localDerivative() {
        this.differential.accept(this);
    }

    public Set<CalcNode> getChildren() {
        return children;
    }

    public List<CalcNode> toList() {
        Set<CalcNode> list = toList(new HashSet<>());
        return list.stream().toList();
    }

    private Set<CalcNode> toList(Set<CalcNode> nodes) {
        Set<CalcNode> kids = this.getChildren();
        nodes.add(this);
        for (CalcNode kid : kids) {
            if (kid.getChildren().isEmpty()) {
                nodes.add(kid);
            } else {
                kid.toList(nodes);
            }
        }
        return nodes;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setGrad(double grad) {
        this.grad = grad;
    }

    public double getGrad() {
        return grad;
    }

    public double getValue() {
        return value;
    }

    public String getOperationSymbol() {
        return this.operation;
    }

    public UUID getNodeId() {
        return nodeId;
    }

    public String getLabel() {
        return label;
    }

    public CalcNode add(CalcNode other) {
        CalcNode out = new CalcNode(this.value + other.value, List.of(this, other), "+");
        out.differential = cn -> {
            this.grad += out.getGrad();
            other.grad += out.getGrad();
        };
        return out;
    }

    public CalcNode add(double other) {
        CalcNode constant = new CalcNode(other, "?");
        CalcNode out = new CalcNode(this.value + constant.value, List.of(this, constant), "+");
        out.differential = cn -> {
            this.grad += out.getGrad();
            constant.grad += out.getGrad();
        };
        return out;
    }

    public CalcNode negate() {
        return this.multiply(-1);
    }

    public CalcNode subtract(double other) {
        CalcNode constant = new CalcNode(other, "?");
        CalcNode out = new CalcNode(this.value - constant.value, List.of(this, constant), "-");
        out.differential = cn -> {
            this.grad += out.getGrad();
            constant.grad += out.getGrad();
        };
        return out;
    }

    public CalcNode subtract(CalcNode other) {
        CalcNode subtracted = this.add(other.negate());
        subtracted.setOperation("-");
        return subtracted;
    }

    public CalcNode multiply(CalcNode other) {
        CalcNode out = new CalcNode(this.value * other.value, List.of(this, other), "*");
        out.differential = cn -> {
            this.grad += (out.getGrad() * other.getValue());
            other.grad += (out.getGrad() * this.getValue());
        };
        return out;
    }

    public CalcNode multiply(double other) {
        CalcNode constant = new CalcNode(other, "?");
        CalcNode out = new CalcNode(this.value * constant.value, List.of(this, constant), "*");
        out.differential = cn -> {
            this.grad += (out.getGrad() * constant.getValue());
            constant.grad += (out.getGrad() * this.getValue());
        };
        return out;
    }

    public CalcNode divide(CalcNode other) {
        return other.powerOf(-1).multiply(this);
    }

    public CalcNode tanh() {
        double t = (Math.exp(2 * this.value) - 1) / (Math.exp(2 * this.value) + 1);
        CalcNode out = new CalcNode(t, List.of(this), "tanh");
        out.differential = cn -> this.grad += ((1 - (t * t)) * out.getGrad());
        return out;
    }

    public CalcNode exp() {
        double exp = Math.exp(this.value);
        CalcNode out = new CalcNode(exp, List.of(this), "e");
        out.differential = cn -> this.grad += out.getValue() * out.getGrad();
        return out;
    }

    public CalcNode powerOf(double power) {
        CalcNode out = new CalcNode(Math.pow(this.value, power), List.of(this), "↑" + power);
        out.differential = cn -> this.grad += power * Math.pow(this.value, (power - 1)) * out.getGrad();
        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalcNode calcNode = (CalcNode) o;
        return this.nodeId.equals(calcNode.nodeId);
    }

    @Override
    public int hashCode() {
        return nodeId.hashCode();
    }
}