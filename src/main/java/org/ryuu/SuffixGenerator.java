package org.ryuu;

import java.util.HashSet;
import java.util.Set;

public class SuffixGenerator {
    private static final HashSet<String> suffixes = new HashSet<>();

    public static void clear() {
        suffixes.clear();
    }

    public static Set<String> get() {
        return suffixes;
    }

    public static void tryAdd(String filePath) {
        int index = filePath.lastIndexOf(".");
        if (index == -1) {
            return;
        }

        suffixes.add(filePath.substring(index));
    }
}