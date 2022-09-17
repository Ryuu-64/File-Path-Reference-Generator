package org.ryuu.filepathreferencegenerator.core;

import org.ryuu.functional.Action;

import java.io.File;
import java.util.Set;

import static org.ryuu.filepathreferencegenerator.core.FieldNameChecker.*;

public class Generator {
    public static final String DEFAULT_REFERENCE_SCRIPT_NAME = "FilePathReference.java";
    public static final Action onStart = new Action();
    public static final Action onOver = new Action();
    private static final SuffixGenerator suffix = new SuffixGenerator();
    private static final Content content = new Content();
    private static FileIgnore fileIgnore;
    private static String rootDirectoryPath;
    private static int indentationDepth = 1;

    public static void generate(String rootDirectoryPath, String referenceScriptPath, String packageName) {
        generate(rootDirectoryPath, referenceScriptPath, packageName, DEFAULT_REFERENCE_SCRIPT_NAME);
    }

    public static void generate(String rootDirectoryPath, String referenceScriptPath, String packageName, String referenceScriptName) {
        if (rootDirectoryPath.equals("")) {
            throw new IllegalArgumentException("root file path can't be null");
        }
        onStart.invoke();
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
            if (file.getName().equals(FileIgnore.FILE_NAME)) {
                fileIgnore = new FileIgnore(file);
                break;
            }
        }

        for (File file : files) {
            if (!file.getName().equals(FileIgnore.FILE_NAME)) {
                write(file);
            }
        }

        addSuffix();
        suffix.clear();
        content.addLine("}");
        content.flush(referenceScriptPath, referenceScriptName);
        onOver.invoke();
    }

    private static void write(File file) {
        String relativePath = getRelativePath(file, rootDirectoryPath);
        boolean isIgnore = fileIgnore != null && fileIgnore.isIgnore(relativePath);
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
        fieldName = fieldName.substring(0, fieldName.length() - 1);
        content.addLineWithTab("public static final String $" + fieldName + " = \"" + relativePath + "\";", indentationDepth);
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
        content.addLineWithTab("public static class $SUFFIX {", indentationDepth);
        indentationDepth++;
        for (String suffix : suffixes) {
            content.addLineWithTab("public static final String " + getLegal(suffix) + " = \"" + suffix + "\";", indentationDepth);
        }
        indentationDepth--;
        content.addLineWithTab("}", indentationDepth);
        content.removeIfEmptyStaticClass();
    }
}