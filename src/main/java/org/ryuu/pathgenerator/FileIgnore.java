package org.ryuu.pathgenerator;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileIgnore {
    public static final String FILE_NAME = ".fileignore";
    private final List<Pattern> ignorePatterns = new ArrayList<>();
    private final List<Pattern> notIgnorePatterns = new ArrayList<>();

    public FileIgnore() {
    }

    public FileIgnore(Path ignore) {
        Objects.requireNonNull(ignore, "The ignore file path cannot be null.");

        ignore = ignore.toAbsolutePath().normalize();

        if (!Files.exists(ignore)) {
            throw new IllegalArgumentException("The ignore file does not exist: " + ignore);
        }

        if (!ignore.getFileName().toString().equals(FILE_NAME)) {
            throw new IllegalArgumentException("The ignore file has an invalid name: " + ignore.getFileName());
        }

        try {
            List<String> ignoreLines = FileUtils.readLines(ignore.toFile(), UTF_8);
            for (String ignoreLine : ignoreLines) {
                if (ignoreLine.trim().isEmpty() || ignoreLine.startsWith("#")) {
                    continue;
                }

                if (ignoreLine.startsWith("!")) {
                    notIgnorePatterns.add(getIgnorePattern(ignoreLine));
                } else {
                    ignorePatterns.add(getIgnorePattern(ignoreLine));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the ignore file: " + ignore, e);
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
        ignoreLine = ".*" + ignoreLine.replace("*", ".*") + ".*";
        return Pattern.compile(ignoreLine);
    }
}