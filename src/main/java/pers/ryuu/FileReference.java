package pers.ryuu;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReference {
    private final List<String> content = new ArrayList<>();

    private int index = -1;

    public void addLineWithTab(String newLine, int tabCount) {
        index++;
        StringBuilder builder = new StringBuilder(newLine);
        while (tabCount > 0) {
            builder.insert(0, "\t");
            tabCount--;
        }
        newLine = builder.toString();
        content.add(index, newLine);
    }

    public void addLine(String newLine) {
        index++;
        content.add(index, newLine);
    }

    public void removeLine() {
        content.remove(index);
        index--;
    }

    public String getLine() {
        return content.get(index);
    }

    public void removeIfEmptyStaticClass() {
        if (content.get(index - 1).endsWith("{") && content.get(index).endsWith("}")) {
            removeLine(); // empty line
            removeLine(); // public static class name {
            removeLine(); // }
        }
    }

    public void write(String referencePath) {
        try (FileWriter fileWriter = new FileWriter(referencePath + "/FileReference.java")) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                for (String line : content) {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}