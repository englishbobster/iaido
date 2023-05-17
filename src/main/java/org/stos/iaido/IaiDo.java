package org.stos.iaido;

public class IaiDo {
    public static void main(String[] args) {
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
        CalcNode addBias = sumInputs.add(bias); addBias.setLabel("n");

        CalcNode output = addBias.tanh(); output.setLabel("output");
        output.backPropagate();
        JsonExporter.toFile(output);
    }
}
