package org.stos.iaido;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(A.getOperation()).isEmpty();
        assertThat(B.getOperation()).isEmpty();
        assertThat(C.getOperation()).isEqualTo("*");
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
}
