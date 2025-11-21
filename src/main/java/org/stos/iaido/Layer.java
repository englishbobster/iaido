package org.stos.iaido;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Layer {

    private List<Neuron> neurons;

    public Layer(int numberOfNeuronsIn, int numberOfNeuronsOut) {
        initializeLayer(numberOfNeuronsIn, numberOfNeuronsOut);
    }

    private void initializeLayer(int in, int out) {
        List<Neuron> neurons = new ArrayList<>();
        IntStream.range(0, out).forEach(i -> neurons.add(new Neuron(in)));
        this.neurons = neurons;
    }

    public Function<List<CalcNode>, List<CalcNode>> prepareLayer() {
        return calcNodes -> IntStream.range(0, neurons.size())
                .boxed()
                .map(i -> neurons.get(i).wireNeuron().apply(calcNodes))
                .toList();
    }
}