package org.stos.iaido;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

public class BackPropagatorTest {

    @Test
    void shouldBackPropagateOnSimpleArithmeticChain() {
        //given
        CalcNode a = new CalcNode(2.0, "a");
        CalcNode b = new CalcNode(-3.0, "b");
        CalcNode c = new CalcNode(10.0, "c");
        CalcNode e = a.multiply(b);
        e.setLabel("e");
        CalcNode d = e.add(c);
        d.setLabel("d");
        CalcNode f = new CalcNode(-2.0, "f");
        CalcNode L = d.multiply(f);
        L.setLabel("L");

        //when
        new BackPropagator().backProp(L);

        //then
        assertThat(a.getGrad()).isEqualTo(6.0);
        assertThat(b.getGrad()).isEqualTo(-4.0);
        assertThat(c.getGrad()).isEqualTo(-2.0);
        assertThat(d.getGrad()).isEqualTo(-2.0);
        assertThat(e.getGrad()).isEqualTo(-2.0);
        assertThat(f.getGrad()).isEqualTo(4.0);
        assertThat(L.getGrad()).isEqualTo(1.0);
    }

    @Test
    void shouldBackPropagateOnPerceptron() {
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
        new BackPropagator().backProp(output);

        assertThat(x0.getGrad()).isCloseTo(-1.5, offset(0.001));
        assertThat(w0.getGrad()).isCloseTo(1, offset(0.001));
        assertThat(x1.getGrad()).isCloseTo(0.5, offset(0.001));
        assertThat(w1.getGrad()).isEqualTo(0);
        assertThat(x0w0.getGrad()).isCloseTo(0.5, offset(0.001));
        assertThat(x1w1.getGrad()).isCloseTo(0.5, offset(0.001));
        assertThat(sumInputs.getGrad()).isCloseTo(0.5, offset(0.001));
        assertThat(bias.getGrad()).isCloseTo(0.5, offset(0.001));
        assertThat(addBias.getGrad()).isCloseTo(0.5, offset(0.001));
        assertThat(output.getGrad()).isEqualTo(1);

    }
}
