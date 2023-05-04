package org.stos.iaido;

import org.stos.iaido.CalcNode.Operation;

import java.util.List;

public class BackPropagator {

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

    public void backProp(CalcNode root) {
        root.setGrad(1.00);
        updateChildren(root);
    }

    private void updateChildren(CalcNode root) {
        if(root.hasChildren()) {
            if (Operation.ADD.equals(root.getOperation())) {
                root.getChildren().forEach(child -> child.setGrad(root.getGrad()));
            }
            if (Operation.MULTIPLY.equals(root.getOperation())) {
                List<CalcNode> children = root.getChildren().stream().toList();
                children.get(0).setGrad(root.getGrad() * children.get(1).getData());
                children.get(1).setGrad(root.getGrad() * children.get(0).getData());
            }
            root.getChildren().forEach(this::updateChildren);
        }
    }
}
