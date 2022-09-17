package org.ryuu.filepathreferencegenerator.core;

import java.util.HashSet;
import java.util.Set;

public class SuffixGenerator {
    private final HashSet<String> suffixes = new HashSet<>();

    public void clear() {
        suffixes.clear();
    }

    public Set<String> get() {
        return suffixes;
    }

    public void tryAdd(String filePath) {
        int index = filePath.lastIndexOf(".");
        if (index == -1) {
            return;
        }

        suffixes.add(filePath.substring(index));
    }
}