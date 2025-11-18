package org.stos.iaido;

public class IaiDo {
    public static void main(String[] args) {
        int tst = 1;
        if(tst == 1){
            CalcNode x = new CalcNode(0.8813735870, "n");

            /*
                         e^2x - 1
            tanh(x) =  ————————————
                         e^2x + 1
            */


            CalcNode x2 = x.multiply(2); x2.setLabel("x2");

            CalcNode e = x2.exp(); e.setLabel("e2x");
            CalcNode esub = e.subtract(1); esub.setLabel("esub");
            CalcNode eadd = e.add(1); eadd.setLabel("eadd");
            CalcNode output = esub.divide(eadd);
            output.backPropagate();
            Grapher.draw(output);
        }
        else if (tst == 2) {
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
            CalcNode n = sumInputs.add(bias);
            n.setLabel("n");

        /*
                         e^2x - 1
            tanh(x) =  ————————————
                         e^2x + 1
        */

            CalcNode e2x = (n.multiply(2)).exp();
            CalcNode e2xMinus = e2x.subtract(1);
            CalcNode e2xPlus = e2x.add(1);

            CalcNode output = e2xMinus.divide(e2xPlus);

            output.backPropagate();
            Grapher.draw(output);

        } else if(tst == 3) {

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