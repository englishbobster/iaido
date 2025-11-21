package org.stos.iaido;

import java.util.List;

public class IaiDo {
    public static void main(String[] args) {
        expFunctionPerceptron();
        tanhFunctionPerceptron();

        List<Double> inputs = List.of(2.0, 3.0, -1.0);
        MLP mlp = new MLP(3, List.of(4, 4, 1));
        List<CalcNode> outputs = mlp.wireMLP().apply(inputs);
        outputs.forEach(System.out::println);
        Grapher.draw(outputs.getFirst(), "bigGraph");
    }

    private static void tanhFunctionPerceptron() {
        //a perceptron with 2 inputs (2D)
        //inputs x0 and x1
        CalcNode
                x0 = new CalcNode(2.0, "x0"),
                x1 = new CalcNode(0.0, "x1");
        //weights w0 and w1
        CalcNode
                w0 = new CalcNode(-3.0, "w0"),
                w1 = new CalcNode(1.0, "w1");
        //neuron bias
        CalcNode bias = new CalcNode(6.8813735870, "bias"); //adjusted according to karpathy video. Gives a nice tanh derivative

        //sum of weighted inputs and bias
        CalcNode x0w0 = x0.multiply(w0); x0w0.setLabel("x0w0");
        CalcNode x1w1 = x1.multiply(w1); x1w1.setLabel("x1w1");
        CalcNode sumInputs = x0w0.add(x1w1); sumInputs.setLabel("summed inputs");
        CalcNode addBias = sumInputs.add(bias); addBias.setLabel("n");

        CalcNode output = addBias.tanh(); output.setLabel("output");

        output.backPropagate();
        Grapher.draw(output, "tanh");
    }

    private static void expFunctionPerceptron() {
        //a perceptron with 2 inputs (2D)
        //inputs x0 and x1
        CalcNode
                x0 = new CalcNode(2.0, "x0"),
                x1 = new CalcNode(0.0, "x1");
        //weights w0 and w1
        CalcNode
                w0 = new CalcNode(-3.0, "w0"),
                w1 = new CalcNode(1.0, "w1");
        //neuron bias
        CalcNode bias = new CalcNode(6.8813735870, "bias"); //adjusted according to karpathy video

        //sum of weighted inputs and bias
        CalcNode x0w0 = x0.multiply(w0); x0w0.setLabel("x0w0");
        CalcNode x1w1 = x1.multiply(w1); x1w1.setLabel("x1w1");
        CalcNode sumInputs = x0w0.add(x1w1); sumInputs.setLabel("summed inputs");
        CalcNode n = sumInputs.add(bias); n.setLabel("n");

        /*
                         e^2x - 1
            tanh(x) =  ————————————
                         e^2x + 1
        */

        CalcNode e2x = (n.multiply(2)).exp(); e2x.setLabel("e2x");
        CalcNode e2xMinus = e2x.subtract(1);  e2xMinus.setLabel("e2x-");
        CalcNode e2xPlus = e2x.add(1); e2xPlus.setLabel("e2x+");

        CalcNode output = e2xMinus.divide(e2xPlus); output.setLabel("output");

        output.backPropagate();
        Grapher.draw(output, "exp_tanh");
    }
}