package org.stos.iaido;

public class IaiDo {
    public static void main(String[] args) {
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

        new BackPropagator().backProp(L);
        JsonExporter.toFile(L);
    }
}
