package org.ryuu.file_path_reference_generator.core;

import org.ryuu.functional.Action;

import java.io.File;
import java.util.Set;

import static org.ryuu.file_path_reference_generator.core.FieldNameChecker.*;

public class Generator {
    public static final Action start = new Action();
    public static final Action over = new Action();
    private static final SuffixGenerator suffix = new SuffixGenerator();
    private static final Content content = new Content();
    private static Ignore ignore;
    private static String rootDirectoryPath;
    private static int indentationDepth = 1;

    public static void generate(String rootDirectoryPath, String referenceScriptPath, String packageName) {
        generate(rootDirectoryPath, referenceScriptPath, packageName, "FilePathReference.java");
    }

    public static void generate(String rootDirectoryPath, String referenceScriptPath, String packageName, String referenceScriptName) {
        if (rootDirectoryPath.equals("")) {
            throw new IllegalArgumentException("root file path can't be null");
        }
        start.invoke();
        Generator.rootDirectoryPath = formatRootFilePath(rootDirectoryPath);
        content.addLine("package " + packageName + ";");
        content.addLine("");
        content.addLine("public class " + referenceScriptName.replace(".java", "") + " {");
        File rootFile = new File(Generator.rootDirectoryPath);
        if (!rootFile.exists()) {
            throw new IllegalArgumentException("unable to get root file, root file path : " + Generator.rootDirectoryPath);
        }
        File[] files = rootFile.listFiles();
        if (files == null) {
            throw new IllegalArgumentException("unable to get subfile in root file, root file path : " + Generator.rootDirectoryPath);
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

        addSuffix();
        suffix.clear();
        content.addLine("}");
        content.write(referenceScriptPath, referenceScriptName);
        over.invoke();
    }

    private static void write(File file) {
        String relativePath = getRelativePath(file, rootDirectoryPath);
        boolean isIgnore = ignore != null && ignore.isIgnore(relativePath);
        if (file.isDirectory()) {
            if (!isIgnore) {
                addDirectory(getLegal(file.getName() + "/"), relativePath);
            }
            addSubfile(file);
        } else if (!isIgnore) {
            addFile(getLegal(file.getName()), relativePath);
        }
    }

    private static String getRelativePath(File file, String prefix) {
        String path = file.getPath();
        path = path.replace(prefix, "");
        path = path.replace('\\', '/');
        return file.isDirectory() ? path + "/" : path;
    }

    private static String formatRootFilePath(String path) {
        path = path.replace('/', '\\');
        if (!path.endsWith("\\")) {
            path = path + "\\";
        }
        return path;
    }

    private static void addDirectory(String fieldName, String relativePath) {
        if (content.getLine().endsWith("}")) {
            content.addLineWithTab("", indentationDepth);
        }
        content.addLineWithTab("public static final String " + fieldName + " = \"" + relativePath + "\";", indentationDepth);
    }

    private static void addSubfile(File file) {
        content.addLineWithTab("", indentationDepth);
        content.addLineWithTab("public static class " + getLegal(file.getName()) + " {", indentationDepth);
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

    private static void addFile(String fieldName, String relativePath) {
        if (content.getLine().endsWith("}")) {
            content.addLineWithTab("", indentationDepth);
        }
        suffix.tryAdd(relativePath);
        content.addLineWithTab("public static final String " + fieldName + " = \"" + relativePath + "\";", indentationDepth);
    }

    private static void addSuffix() {
        Set<String> suffixes = suffix.get();
        if (suffixes.size() == 0) {
            return;
        }
        indentationDepth = 1;
        content.addLineWithTab("", indentationDepth);
        content.addLineWithTab("public static class $suffix {", indentationDepth);
        indentationDepth++;
        for (String suffix : suffixes) {
            content.addLineWithTab("public static final String " + getLegal(suffix) + " = \"" + suffix + "\";", indentationDepth);
        }
        indentationDepth--;
        content.addLineWithTab("}", indentationDepth);
        content.removeIfEmptyStaticClass();
    }
}