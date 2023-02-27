package org.ryuu.pathgenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class FileIgnore {
    public static final String FILE_NAME = ".fileignore";
    private final List<Pattern> ignorePatterns = new ArrayList<>();
    private final List<Pattern> notIgnorePatterns = new ArrayList<>();

    public FileIgnore() {
    }

    public FileIgnore(Path ignoreFile) {
        Objects.requireNonNull(ignoreFile, "The ignore file path cannot be null.");

        ignoreFile = ignoreFile.toAbsolutePath().normalize();

        if (!Files.exists(ignoreFile)) {
            throw new IllegalArgumentException("The ignore file does not exist: " + ignoreFile);
        }

        if (!ignoreFile.getFileName().toString().equals(FILE_NAME)) {
            throw new IllegalArgumentException("The ignore file has an invalid name: " + ignoreFile.getFileName());
        }

        try (BufferedReader reader = Files.newBufferedReader(ignoreFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                if (line.startsWith("!")) {
                    notIgnorePatterns.add(getIgnorePattern(line));
                } else {
                    ignorePatterns.add(getIgnorePattern(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the ignore file: " + ignoreFile, e);
        }
    }

    public boolean isIgnorePath(String path) {
        return ignorePatterns.stream().anyMatch(pattern -> pattern.matcher(path).matches()) &&
                notIgnorePatterns.stream().noneMatch(pattern -> pattern.matcher(path).matches());
    }

    private static Pattern getIgnorePattern(String ignoreLine) {
        if (ignoreLine.startsWith("!")) {
            ignoreLine = ignoreLine.substring(1);
        }
        ignoreLine = ".*" + ignoreLine.replace("*", ".*");
        return Pattern.compile(ignoreLine);
    }
}