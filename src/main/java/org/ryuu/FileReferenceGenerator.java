package org.ryuu;

import java.io.File;
import java.util.Set;

public class FileReferenceGenerator {
    private FileReferenceGenerator() {
    }

    public static void main(String[] args) {
        new FileReferenceGenerator().generate(
                "E:\\SvnWorkspace\\cooyocode\\fingerhockey\\tags\\AirHockey\\Air-Hockey\\android\\assets",
                "E:\\SvnWorkspace\\cooyocode\\fingerhockey\\tags\\AirHockey\\Air-Hockey\\core\\src\\com\\coolstudios\\air_hockey",
                "com.coolstudios.air_hockey"
        );
    }

    private final SuffixGenerator suffix = new SuffixGenerator();
    private final FileContent reference = new FileContent();
    private FileIgnore fileIgnore;
    private String rootFilePath;
    private int depth = 1;

    public void generate(String rootFilePath, String referencePath, String packageName) {
        this.rootFilePath = formatRootFilePath(rootFilePath);
        reference.addLine("package " + packageName + ";");
        reference.addLine("");
        reference.addLine("public class FileReference {");
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
                fileIgnore = new FileIgnore(file);
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
        reference.addLine("}");
        reference.write(referencePath);
    }

    private void write(File file) {
        String relativePath = getRelativePath(file, rootFilePath);
        boolean isIgnore = fileIgnore != null && fileIgnore.isIgnore(relativePath);
        String fieldName = FieldNameProcessor.getLegal(file.getName());
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
        if (reference.getLine().endsWith("}")) {
            reference.addLineWithTab("", depth);
        }
        reference.addLineWithTab("public static final String " + fieldName + "$directory = \"" + relativePath + "\";", depth);
    }

    private void writeSubfile(File file) {
        reference.addLineWithTab("", depth);
        reference.addLineWithTab("public static class " + FieldNameProcessor.getLegal(file.getName()) + " {", depth);
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

    private void writeFile(String fieldName, String relativePath) {
        if (reference.getLine().endsWith("}")) {
            reference.addLineWithTab("", depth);
        }
        suffix.tryAdd(relativePath);
        reference.addLineWithTab("public static final String " + fieldName + " = \"" + relativePath + "\";", depth);
    }

    private void writeSuffix() {
        Set<String> suffixes = suffix.get();
        if (suffixes.size() == 0) {
            return;
        }
        depth = 1;
        reference.addLineWithTab("", depth);
        reference.addLineWithTab("public static class $suffix {", depth);
        depth++;
        for (String suffix : suffixes) {
            reference.addLineWithTab("public static final String " + FieldNameProcessor.getLegal(suffix) + " = \"" + suffix + "\";", depth);
        }
        depth--;
        reference.addLineWithTab("}", depth);
        reference.removeIfEmptyStaticClass();
    }
}