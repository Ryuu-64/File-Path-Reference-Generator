package pers.ryuu;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileReferenceGenerator {
    // TODO update readme
    // TODO .fileignore regex
    public static void main(String[] args) {
        generate(
                "E:\\Air-Hockey\\assets\\",
                "E:\\Air-Hockey\\core\\src\\com\\coolstudios\\air_hockey\\",
                "com.coolstudios.air_hockey"
        );
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
                continue;
            }
            write(file);
        }
        reference.addLine("}");
        reference.write(referencePath);
    }

    private static void write(File file) {
        String relativePath = getRelativePath(file, rootFilePath);
        boolean isIgnore = fileIgnore.isIgnore(relativePath);
        String fieldName = formatFileFieldName(file.getName());
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
        reference.addLineWithTab("public static class " + formatFileFieldName(file.getName()) + " {", depth);
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
        return file.isDirectory() ? getLegalFieldName(path + "/") : getLegalFieldName(path);
    }

    private static String formatFileFieldName(String path) {
        path = path.replace(' ', '_');
        path = path.replace('-', '_');
        path = path.replace('.', '_');
        path = path.replace('/', '_');
        path = path.replace('\\', '_');
        path = getLegalFieldName(path);
        return path;
    }

    private static String formatRootFilePath(String path) {
        path = path.replace('/', '\\');
        if (!path.endsWith("\\")) {
            path = path + "\\";
        }
        return path;
    }

    private static String getLegalFieldName(String name) {
        Pattern startWithNumberPattern = Pattern.compile("\\d");
        Matcher matcher = startWithNumberPattern.matcher(name.charAt(0) + "");
        if (matcher.matches()) {
            return "$" + name;
        } else {
            return name;
        }
    }
}