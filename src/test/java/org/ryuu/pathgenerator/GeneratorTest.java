package org.ryuu.pathgenerator;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

class GeneratorTest {
    @Test
    void generate() {
        new Generator().generate(
                Paths.get("./src/test/resources"),
                Paths.get("./src/test/java/org/ryuu/pathgenerator"),
                "org.ryuu.pathgenerator"
        );
    }
}