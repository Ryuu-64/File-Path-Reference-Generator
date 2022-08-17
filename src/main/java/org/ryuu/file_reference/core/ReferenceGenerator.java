package org.ryuu.file_reference.core;

import org.ryuu.functional.Action;

import java.io.File;
import java.util.Set;

public class ReferenceGenerator {
    public final Action generateStart = new Action();
    public final Action generateOver = new Action();
    private final SuffixGenerator suffix = new SuffixGenerator();
    private final Content content = new Content();
    private Ignore ignore;
    private String rootFilePath;
    private int indentationDepth = 1;

    public void generate(String rootFilePath, String referencePath, String packageName, String scriptName) {
        generateStart.invoke();
        this.rootFilePath = formatRootFilePath(rootFilePath);
        content.addLine("package " + packageName + ";");
        content.addLine("");
        content.addLine("public class FileReference {");
        File rootFile = new File(this.rootFilePath);
        if (!rootFile.exists()) {
            throw new IllegalArgumentException("unable to get root file, root file path : " + this.rootFilePath);
        }
        File[] files = rootFile.listFiles();
        if (files == null) {
            throw new IllegalArgumentException("unable to get subfile in root file, root file path : " + this.rootFilePath);
        }
        for (File file : files) {
            if (file.getName().equals(".fileignore")) {
                ignore = new Ignore(file);
                break;
            }
        }

        for (File file : files) {
            if (!file.getName().equals(".fileignore")) {
                write(file);
            }
        }

        writeSuffix();
        suffix.clear();
        content.addLine("}");
        content.write(referencePath, scriptName);
        generateOver.invoke();
    }

    private void write(File file) {
        String relativePath = getRelativePath(file, rootFilePath);
        boolean isIgnore = ignore != null && ignore.isIgnore(relativePath);
        String fieldName = FieldNameChecker.getLegal(file.getName());
        if (file.isDirectory()) {
            if (!isIgnore) {
                writeDirectory(fieldName, relativePath);
            }
            writeSubfile(file);
        } else if (!isIgnore) {
            writeFile(fieldName, relativePath);
        }
    }

    private String getRelativePath(File file, String prefix) {
        String path = file.getPath();
        path = path.replace(prefix, "");
        path = path.replace('\\', '/');
        return file.isDirectory() ? path + "/" : path;
    }

    private String formatRootFilePath(String path) {
        path = path.replace('/', '\\');
        if (!path.endsWith("\\")) {
            path = path + "\\";
        }
        return path;
    }

    private void writeDirectory(String fieldName, String relativePath) {
        if (content.getLine().endsWith("}")) {
            content.addLineWithTab("", indentationDepth);
        }
        content.addLineWithTab("public static final String " + fieldName + "$directory = \"" + relativePath + "\";", indentationDepth);
    }

    private void writeSubfile(File file) {
        content.addLineWithTab("", indentationDepth);
        content.addLineWithTab("public static class " + FieldNameChecker.getLegal(file.getName()) + " {", indentationDepth);
        indentationDepth++;
        File[] files = file.listFiles();
        if (files == null) {
            throw new RuntimeException("no subfile in file, file path : " + file.getAbsolutePath());
        }
        for (File childFile : files) {
            write(childFile);
        }
        indentationDepth--;
        content.addLineWithTab("}", indentationDepth);
        content.removeIfEmptyStaticClass();
    }

    private void writeFile(String fieldName, String relativePath) {
        if (content.getLine().endsWith("}")) {
            content.addLineWithTab("", indentationDepth);
        }
        suffix.tryAdd(relativePath);
        content.addLineWithTab("public static final String " + fieldName + " = \"" + relativePath + "\";", indentationDepth);
    }

    private void writeSuffix() {
        Set<String> suffixes = suffix.get();
        if (suffixes.size() == 0) {
            return;
        }
        indentationDepth = 1;
        content.addLineWithTab("", indentationDepth);
        content.addLineWithTab("public static class $suffix {", indentationDepth);
        indentationDepth++;
        for (String suffix : suffixes) {
            content.addLineWithTab("public static final String " + FieldNameChecker.getLegal(suffix) + " = \"" + suffix + "\";", indentationDepth);
        }
        indentationDepth--;
        content.addLineWithTab("}", indentationDepth);
        content.removeIfEmptyStaticClass();
    }
}