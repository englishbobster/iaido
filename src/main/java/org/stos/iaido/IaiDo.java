package org.stos.iaido;

public class IaiDo {
    public static void main(String[] args) {
        CalcNode A = new CalcNode(-3.0, "A");
        CalcNode B = new CalcNode(2.0, "B");
        CalcNode C = A.multiply(B);
        C.setLabel("C");

        CalcNode D = new CalcNode(10.0, "D");
        CalcNode E = C.add(D);
        E.setLabel("E");

        JsonExporter.toFile(E);
    }
}
