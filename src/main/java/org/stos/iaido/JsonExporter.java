package org.stos.iaido;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class JsonExporter {

    public static void toFile(CalcNode root){
        String collect = root.toList().stream()
                .map(CalcNode::toString)
                .collect(Collectors.joining(",\n", "[\n", "\n]"));
        try {
            Path destination = Path.of("./output/nodes.json");
            Files.createDirectories(destination.getParent());
            Files.writeString(destination, collect);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
