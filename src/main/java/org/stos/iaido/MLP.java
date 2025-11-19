package org.stos.iaido;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class MLP {
    private List<Layer> layers;

    public MLP(int numberOfInputs, List<Integer> neuronsOutPerLayer) {
        initializeLayers(numberOfInputs, neuronsOutPerLayer);
    }

    private void initializeLayers(int numberOfInputs, List<Integer> neuronsOutPerLayer) {
        List<Layer> layers = new ArrayList<>();
        List<Integer> inAndOut = new ArrayList<>();
        inAndOut.add(numberOfInputs);
        inAndOut.addAll(neuronsOutPerLayer);

        IntStream.range(0, neuronsOutPerLayer.size()).boxed()
                .forEach(i -> {
                    Layer layer = new Layer(inAndOut.get(i), inAndOut.get(i + 1));
                    layers.add(layer);
                });
        this.layers = layers;
    }

    public Function<List<Double>, List<CalcNode>> wireMLP() {
        return doubles -> layers.stream()
                .map(layer -> layer.prepareLayer().apply(doubles))
                .reduce((calcsA, calcsB) -> calcsB).orElseThrow();
    }

}