package org.stos.iaido;

public class IaiDo {
    public static void main(String[] args) {
        boolean complicated = false;

        if(complicated){
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
            tanh =  ————————————
                      e^2x + 1
        */

            CalcNode na = n.multiply(2); na.setLabel("na");
            CalcNode nb = n.multiply(2); nb.setLabel("nb");

            CalcNode e1 = na.exp(); e1.setLabel("e1");
            CalcNode e2 = nb.exp(); e2.setLabel("e2");
            CalcNode e1add = e1.add(-1); e1add.setLabel("e1add");
            CalcNode e2add = e2.add(1); e2add.setLabel("e2add");
            CalcNode output = e1add.powerDivide(e2add);

            output.backPropagate();
            Grapher.draw(output);

        } else {

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
            CalcNode x0w0 = x0.multiply(w0);
            x0w0.setLabel("x0w0");
            CalcNode x1w1 = x1.multiply(w1);
            x1w1.setLabel("x1w1");
            CalcNode sumInputs = x0w0.add(x1w1);
            sumInputs.setLabel("summed inputs");
            CalcNode addBias = sumInputs.add(bias);
            addBias.setLabel("n");

            CalcNode output = addBias.tanh();
            output.setLabel("output");
            output.backPropagate();
            Grapher.draw(output);
        }

    }
}