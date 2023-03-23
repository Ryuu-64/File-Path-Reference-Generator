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
import static org.ryuu.pathgenerator.FileIgnore.FILE_NAME;

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
        Path targetFilePath = Paths.get(
                targetFolder.toString() + "/" +
                IdentifierUtils.legal(sourceFolder.getFileName().toString()) + ".java"
        );
        try {
            FileUtils.writeStringToFile(
                    targetFilePath.toFile(),
                    javaFile.toString(),
                    UTF_8
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TypeSpec createClass(Path directoryPath) {
        if (!Files.isDirectory(directoryPath)) {
            return null;
        }

        String identifier = IdentifierUtils.legal(directoryPath.getFileName().toString());
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(identifier).addModifiers(PUBLIC, FINAL);
        // top-level class cannot be declared as static
        if (directoryPath != this.sourcePath) {
            classBuilder.addModifiers(STATIC);
        }
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directoryPath)) {
            for (Path innerPath : directoryStream) {
                if (!fileIgnore.isIgnorePath(PathUtils.toStringForwardSlash(innerPath))) {
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

        // avoid generating empty class
        if (classBuilder.typeSpecs.size() == 0 && classBuilder.fieldSpecs.size() == 0) {
            return null;
        }

        return classBuilder.build();
    }

    private FieldSpec createField(Path path) {
        String identifier = IdentifierUtils.legal(path.getFileName().toString());
        String pathString = PathUtils.toStringForwardSlash(path);
        if (Files.isDirectory(path)) {
            // the $ prefix needs to be added
            // otherwise the field may conflict with the class
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