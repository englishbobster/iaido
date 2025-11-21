package org.stos.iaido;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Neuron {
    private List<CalcNode> weights;
    private CalcNode bias;
    private final Random random;

    public Neuron(int numberOfInputs) {
        random = new Random();
        initializeWeights(numberOfInputs);
        initializeBias();
    }

    private void initializeBias() {
        bias = new CalcNode(random.nextDouble(-1.0, 1.0), "b");
    }

    private void initializeWeights(int numberOfInputs) {
        List<CalcNode> nodes = new ArrayList<>();
        IntStream.range(0, numberOfInputs)
                .forEach(i -> nodes.add(
                                new CalcNode(random.nextDouble(-1.0, 1.0), "w" + i)
                        )
                );
        weights = nodes;
    }

    public Function<List<CalcNode>, CalcNode> wireNeuron() {
        return doubles -> {
            int size = Math.min(weights.size(), doubles.size());
            CalcNode activation = IntStream.range(0, size).boxed()
                    .map(i -> weights.get(i).multiply(doubles.get(i)))
                    .reduce(CalcNode::add).orElseThrow().add(bias);
            return activation.tanh();
        };
    }

}