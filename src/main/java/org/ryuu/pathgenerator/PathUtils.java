package org.ryuu.pathgenerator;

import java.nio.file.Path;

public class PathUtils {
    private PathUtils() {
    }

    /**
     * When call the toString() method on a Path object,
     * the resulting string representation of the path will use the file separator character for the underlying operating system.
     * On Windows, for example, the file separator character is the backslash \.
     */
    public static String toStringForwardSlash(Path path) {
        return path.toString().replaceAll("\\\\", "/");
    }
}