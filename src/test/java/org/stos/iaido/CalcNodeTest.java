package org.stos.iaido;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

public class CalcNodeTest {

    @Test
    void addsCompNodes() {
        CalcNode A = new CalcNode(3.0, "A");
        CalcNode B = new CalcNode(7.0, "B");
        CalcNode C = A.add(B);
        assertThat(C).isEqualTo(new CalcNode(10.0, ""));
    }

    @Test
    void multipliesCompNodes() {
        CalcNode A = new CalcNode(3.0, "A");
        CalcNode B = new CalcNode(7.0, "B");
        CalcNode C = A.multiply(B);

        assertThat(C).isEqualTo(new CalcNode(21.0, ""));
    }

    @Test
    void multiplyAndAddKeepingPrecedence() {
        CalcNode A = new CalcNode(10.0, "A");
        CalcNode B = new CalcNode(10.0, "B");
        CalcNode C = new CalcNode(3.0, "C");

        CalcNode D = (A.multiply(B)).add(C);
        assertThat(D).isEqualTo(new CalcNode(103.0, ""));

        D = (A.multiply(C)).add(B);
        assertThat(D).isEqualTo(new CalcNode(40, ""));
    }

    @Test
    void resultSetsChildrenCorrectly() {
        CalcNode A = new CalcNode(10.0, "A");
        CalcNode B = new CalcNode(4.0, "B");
        CalcNode C = A.multiply(B);
        assertThat(A.getChildren()).hasSize(0);
        assertThat(B.getChildren()).hasSize(0);
        assertThat(C.getChildren()).hasSize(2);
    }

    @Test
    void resultSetsOperationCorrectly() {
        CalcNode A = new CalcNode(10.0, "A");
        CalcNode B = new CalcNode(4.0, "B");
        CalcNode C = A.multiply(B);
        System.out.println(C);
        assertThat(A.getOperationSymbol()).isEmpty();
        assertThat(B.getOperationSymbol()).isEmpty();
        assertThat(C.getOperationSymbol()).isEqualTo("*");
    }

    @Test
    void listifyParentAndChildren_3_nodes() {
        CalcNode A = new CalcNode(10.0, "A");
        CalcNode B = new CalcNode(4.0, "B");
        CalcNode C = A.multiply(B);

        assertThat(C.toList()).containsExactlyInAnyOrder(A, B, C);
    }

    @Test
    void listifyParentAndChildren_5_nodes() {
        CalcNode A = new CalcNode(-3.0, "A");
        CalcNode B = new CalcNode(2.0, "B");
        CalcNode C = A.multiply(B);
        C.setLabel("C");
        CalcNode D = new CalcNode(10.0, "D");
        CalcNode E = C.add(D);
        E.setLabel("E");
        assertThat(E.toList()).containsExactlyInAnyOrder(A, B, C, D, E);
    }

    @Test
    void parentConsumerPropagatesGradCorrectlyWhenAdding() {
        CalcNode A = new CalcNode(1, "A");
        CalcNode B = new CalcNode(1, "B");
        CalcNode C =  A.add(B);
        C.setGrad(10.0);

        C.backprop();
        assertThat(C.getGrad()).isEqualTo(10.0);
        assertThat(A.getGrad()).isEqualTo(10.0);
        assertThat(B.getGrad()).isEqualTo(10.0);
    }

    @Test
    void parentConsumerPropagatesGradCorrectlyWhenMultiplying() {
        CalcNode A = new CalcNode(2, "A");
        CalcNode B = new CalcNode(3, "B");
        CalcNode C =  A.multiply(B);
        C.setGrad(10.0);

        C.backprop();
        assertThat(C.getGrad()).isEqualTo(10.0);
        assertThat(A.getGrad()).isEqualTo(30.0);
        assertThat(B.getGrad()).isEqualTo(20.0);
    }

    @Test
    void parentConsumerPropagatesGradCorrectlyWhenTanh() {
        CalcNode A = new CalcNode(0.881, "A");
        CalcNode B = A.tanh();
        B.setGrad(1.0);

        B.backprop();
        assertThat(A.getGrad()).isCloseTo(0.5, offset(0.0005));
    }
}
