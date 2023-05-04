package org.stos.iaido;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BackPropagatorTest {

    @Test
    void shouldBackPropagate() {
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

        assertThat(a.getGrad()).isEqualTo(6.0);
        assertThat(b.getGrad()).isEqualTo(-4.0);
        assertThat(c.getGrad()).isEqualTo(-2.0);
        assertThat(d.getGrad()).isEqualTo(-2.0);
        assertThat(e.getGrad()).isEqualTo(-2.0);
        assertThat(f.getGrad()).isEqualTo(4.0);
        assertThat(L.getGrad()).isEqualTo(1.0);
    }
}
