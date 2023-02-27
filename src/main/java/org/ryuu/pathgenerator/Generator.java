package org.ryuu.pathgenerator;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.lang.model.element.Modifier.*;
import static org.ryuu.pathgenerator.FileIgnore.*;

public class Generator {
    private Path sourcePath;
    private FileIgnore fileIgnore;

    public void generate(Path sourceFolder, Path targetFolder, String packageName) {
        if (!Files.exists(sourceFolder)) {
            return;
        }

        if (!Files.isDirectory(sourceFolder)) {
            return;
        }

        fileIgnore = readFileIgnore(sourceFolder);

        this.sourcePath = sourceFolder;
        TypeSpec klass = createClass(sourceFolder);
        if (klass == null) {
            return;
        }

        JavaFile javaFile = JavaFile.builder(packageName, klass).build();
        Path targetFilePath = Paths.get(targetFolder.toString() + "/$" + sourceFolder.getFileName() + ".java");
        try {
            FileUtils.writeStringToFile(
                    targetFilePath.toFile(),
                    javaFile.toString()
                    , UTF_8
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TypeSpec createClass(Path directoryPath) {
        if (!Files.isDirectory(directoryPath)) {
            return null;
        }

        String identifier = IdentifierProcessor.getLegal(directoryPath.getFileName().toString());
        TypeSpec.Builder classBuilder;
        if (directoryPath != this.sourcePath) {
            classBuilder = TypeSpec.classBuilder(identifier);
        } else {
            classBuilder = TypeSpec.classBuilder("$" + identifier);
        }
        classBuilder.addModifiers(PUBLIC, FINAL);
        if (directoryPath != this.sourcePath) {
            classBuilder.addModifiers(STATIC);
        }
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directoryPath)) {
            for (Path innerPath : directoryStream) {
                if (!fileIgnore.isIgnorePath(innerPath.toString())) {
                    classBuilder.addField(createField(innerPath));
                }

                if (Files.isDirectory(innerPath)) {
                    TypeSpec typeSpec = createClass(innerPath);
                    if (typeSpec != null) {
                        classBuilder.addType(typeSpec);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return classBuilder.build();
    }

    private FieldSpec createField(Path path) {
        String identifier = IdentifierProcessor.getLegal(path.getFileName().toString());
        String pathString = path.normalize().toString().replaceAll("\\\\", "/");
        if (Files.isDirectory(path)) {
            identifier = "$" + identifier;
        }
        return FieldSpec
                .builder(String.class, identifier, PUBLIC, STATIC, FINAL)
                .initializer("$S", pathString)
                .build();
    }

    private FileIgnore readFileIgnore(Path sourcePath) {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sourcePath)) {
            for (Path innerPath : directoryStream) {
                if (!Files.isRegularFile(innerPath)) {
                    continue;
                }

                if (!innerPath.getFileName().toString().equals(FILE_NAME)) {
                    continue;
                }

                return new FileIgnore(innerPath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new FileIgnore();
    }
}