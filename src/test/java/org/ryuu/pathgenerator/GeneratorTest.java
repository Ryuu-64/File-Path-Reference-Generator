package org.ryuu.pathgenerator;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeneratorTest {
    @Test
    void illegalRootFolderName() {
        new Generator().generate(
                Paths.get("./src/test/resources/illegal-root-folder-name"),
                Paths.get("./src/test/java/org/ryuu/pathgenerator/generated"),
                "org.ryuu.pathgenerator.generated"
        );

        try {
            Class<?> klass = Class.forName("org.ryuu.pathgenerator.generated.illegal_root_folder_name");
            assertEquals(klass.getDeclaredClasses().length, 0);
            assertEquals(klass.getDeclaredFields().length, 1);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void folderIgnore() {
        new Generator().generate(
                Paths.get("./src/test/resources/folderIgnore"),
                Paths.get("./src/test/java/org/ryuu/pathgenerator/generated"),
                "org.ryuu.pathgenerator.generated"
        );

        try {
            Class<?> klass = Class.forName("org.ryuu.pathgenerator.generated.folderIgnore");
            assertEquals(klass.getDeclaredClasses().length, 1);
            assertEquals(klass.getDeclaredFields().length, 0);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void filesInFolderIgnore() {
        new Generator().generate(
                Paths.get("./src/test/resources/filesInFolderIgnore"),
                Paths.get("./src/test/java/org/ryuu/pathgenerator/generated/"),
                "org.ryuu.pathgenerator.generated"
        );

        try {
            Class<?> klass = Class.forName("org.ryuu.pathgenerator.generated.filesInFolderIgnore");
            assertEquals(klass.getDeclaredClasses().length, 0);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
