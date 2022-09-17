package org.ryuu.filepathreferencegenerator.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileIgnore {
    public static final String FILE_NAME = ".fileignore";
    private final List<String> ignorePatterns = new ArrayList<>();
    private final List<String> notIgnorePatterns = new ArrayList<>();

    public FileIgnore(File ignoreFile) {
        read(ignoreFile);
    }

    public boolean isIgnore(String path) {
        boolean isIgnore = false;
        for (String pattern : ignorePatterns) {
            if (Pattern.matches(pattern, path)) {
                isIgnore = true;
                break;
            }
        }

        if (isIgnore) {
            for (String pattern : notIgnorePatterns) {
                if (Pattern.matches(pattern, path)) {
                    isIgnore = false;
                    break;
                }
            }
        }
        return isIgnore;
    }

    private void read(File ignoreFile) {
        if (ignoreFile == null) {
            throw new NullPointerException();
        }

        if (!ignoreFile.exists()) {
            throw new IllegalArgumentException();
        }

        if (!ignoreFile.getName().equals(FILE_NAME)) {
            throw new IllegalArgumentException();
        }

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(ignoreFile.toPath())))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equals("") || line.startsWith("#")) {
                    continue;
                }

                if (line.startsWith("!")) {
                    line = line.replaceFirst("!", "");
                    if (line.contains("*")) {
                        line = line.replace("*", ".*");
                    }
                    line = ".*" + line;
                    notIgnorePatterns.add(line);
                } else {
                    if (line.contains("*")) {
                        line = line.replace("*", ".*");
                    }
                    line = ".*" + line;
                    ignorePatterns.add(line);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}