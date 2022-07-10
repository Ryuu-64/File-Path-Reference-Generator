package pers.ryuu;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileReferenceGenerator {
    private static final ArrayList<String> fileContent = new ArrayList<>(1 << 10);
    private static String assetPath;
    private static String destinationPath;
    private static int lineIndex = 0;

    /**
     * 生成指定资源文件夹中所有文件的相对路径字符串的字符串字段引用
     * 生成类的层级结构与文件夹一致
     *
     * @param assetPath       资源文件夹的绝对路径
     * @param destinationPath 生成类的目标文件夹绝对路径
     */

    public static void generate(String assetPath, String destinationPath) {
        FileReferenceGenerator.assetPath = dealInputFolderPath(assetPath);
        FileReferenceGenerator.destinationPath = dealInputFolderPath(destinationPath);

        addLine("package " + getPackageName(FileReferenceGenerator.destinationPath) + ";");
        addLine("");
        addLine("public class FileReference {");
        File file = new File(FileReferenceGenerator.assetPath);
        File[] files = file.listFiles();
        if (files == null) {
            throw new IllegalArgumentException("unable to get asset file: " + FileReferenceGenerator.assetPath);
        }
        for (File childFile : files) {
            write(childFile);
        }
        addLine("}");
        addLine("");
        writeAssetReference();
    }

    private static void write(File file) {
        if (file.isDirectory()) {
            String filePath = file.getPath();
            String relativeFilePath = getRelativeFilePath(filePath, assetPath);
            File[] files = file.listFiles();
            assert files != null;
            List<String> nameList = Arrays.asList(Objects.requireNonNull(file.list()));
            if (!nameList.contains("ignoreFolder")) {
                addLine("public static final String " + file.getName() + "_folder = \"" + relativeFilePath + "/\";");
                addLine("");
            }
            if (!nameList.contains("ignoreFile")) {
                addLine("public static class " + file.getName() + "{");
                for (File childFile : files) {
                    write(childFile);
                }
                addLine("}");
            }
        } else {
            String relativeFilePath = getRelativeFilePath(file.getPath(), assetPath);
            String relativeFilePathReference = getRelativeFilePathReference(file.getName(), assetPath);
            addLine("public static final String " + relativeFilePathReference + " = \"" + relativeFilePath + "\";");
        }
    }

    private static void addLine(String newLine) {
        fileContent.add(lineIndex, newLine);
        lineIndex++;
    }

    private static String getPackageName(String destinationPath) {
        String packageStartPath = "\\src\\main\\java\\";
        int srcIndex = destinationPath.indexOf(packageStartPath);
        String packageName = destinationPath.substring(srcIndex);
        packageName = packageName.replace(packageStartPath, "");
        if (packageName.endsWith("\\")) {
            packageName = packageName.substring(0, packageName.lastIndexOf("\\"));
        }
        packageName = packageName.replace("\\", ".");
        return packageName;
    }

    private static String getRelativeFilePath(String path, String prefix) {
        path = path.replace(prefix, "");
        path = path.replace('\\', '/');
        return dealStartWithNumber(path);
    }

    private static String getRelativeFilePathReference(String path, String prefix) {
        path = path.replace(prefix, "");
        path = path.replace(' ', '_');
        path = path.replace('-', '_');
        path = path.replace('.', '_');
        path = path.replace('/', '_');
        path = path.replace('\\', '_');
        return dealStartWithNumber(path);
    }

    private static void writeAssetReference() {
        try (FileWriter fileWriter = new FileWriter(destinationPath + "/FileReference.java")) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                for (String line : fileContent) {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String dealInputFolderPath(String path) {
        path = path.replace('/', '\\');
        if (!path.endsWith("\\")) {
            path = path + "\\";
        }
        return path;
    }

    private static String dealStartWithNumber(String fileName) {
        Pattern startWithNumberPattern = Pattern.compile("\\d");
        Matcher matcher = startWithNumberPattern.matcher(fileName.charAt(0) + "");
        if (matcher.matches()) {
            return "$" + fileName;
        } else {
            return fileName;
        }
    }
}