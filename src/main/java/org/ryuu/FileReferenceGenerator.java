package org.ryuu;

import java.io.File;

import static org.ryuu.FieldNameProcessor.*;

public class FileReferenceGenerator {
    private FileReferenceGenerator() {
    }

    private static final FileReference reference = new FileReference();
    private static FileIgnore fileIgnore;
    private static String rootFilePath;
    private static int depth = 1;

    public static void generate(String rootFilePath, String referencePath, String packageName) {
        FileReferenceGenerator.rootFilePath = formatRootFilePath(rootFilePath);
        reference.addLine("package " + packageName + ";");
        reference.addLine("");
        reference.addLine("public class FileReference {");
        File rootFile = new File(FileReferenceGenerator.rootFilePath);
        if (!rootFile.exists()) {
            throw new IllegalArgumentException("unable to get root file, root file path : " + FileReferenceGenerator.rootFilePath);
        }
        File[] files = rootFile.listFiles();
        if (files == null) {
            throw new IllegalArgumentException("unable to get subfile in root file, root file path : " + FileReferenceGenerator.rootFilePath);
        }
        for (File file : files) {
            if (file.getName().equals(".fileignore")) {
                fileIgnore = new FileIgnore(file);
                break;
            }
        }

        for (File file : files) {
            if (!file.getName().equals(".fileignore")) {
                write(file);
            }
        }

        reference.addLine("}");
        reference.write(referencePath);
    }

    private static void write(File file) {
        String relativePath = getRelativePath(file, rootFilePath);
        boolean isIgnore = fileIgnore == null || fileIgnore.isIgnore(relativePath);
        String fieldName = getLegal(file.getName());
        if (file.isDirectory()) {
            if (!isIgnore) {
                writeDirectory(fieldName, relativePath);
            }
            writeSubfile(file);
        } else if (!isIgnore) {
            writeFile(fieldName, relativePath);
        }
    }

    private static void writeSubfile(File file) {
        reference.addLineWithTab("", depth);
        reference.addLineWithTab("public static class " + getLegal(file.getName()) + " {", depth);
        depth++;
        File[] files = file.listFiles();
        if (files == null) {
            throw new RuntimeException("no subfile in file, file path : " + file.getAbsolutePath());
        }
        for (File childFile : files) {
            write(childFile);
        }
        depth--;
        reference.addLineWithTab("}", depth);
        reference.removeIfEmptyStaticClass();
    }

    private static void writeDirectory(String fieldName, String relativePath) {
        if (reference.getLine().endsWith("}")) {
            reference.addLineWithTab("", depth);
        }
        reference.addLineWithTab("public static final String " + fieldName + "$directory = \"" + relativePath + "\";", depth);
    }

    private static void writeFile(String fieldName, String relativePath) {
        if (reference.getLine().endsWith("}")) {
            reference.addLineWithTab("", depth);
        }
        reference.addLineWithTab("public static final String " + fieldName + " = \"" + relativePath + "\";", depth);
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
}