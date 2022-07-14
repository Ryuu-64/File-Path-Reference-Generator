package pers.ryuu;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileReferenceGenerator {
    public static void main(String[] args) {
        generate("E:\\LibgdxWorkSpace\\Air-Hockey\\assets", "E:\\LibgdxWorkSpace\\Air-Hockey\\core\\src\\com\\coolstudios\\airhockey");
    }

    private static final ArrayList<String> fileReferenceContent = new ArrayList<>(1 << 10);
    private static final ArrayList<String> ignoreFiles = new ArrayList<>(1 << 5);
    private static String rootPath;
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
        FileReferenceGenerator.rootPath = dealInputFolderPath(assetPath);
        FileReferenceGenerator.destinationPath = dealInputFolderPath(destinationPath);

        addLine("package " + getPackageName(FileReferenceGenerator.destinationPath) + ";");
        addLine("");
        addLine("public class FileReference {");
        File rootFile = new File(FileReferenceGenerator.rootPath);
        File[] files = rootFile.listFiles();
        if (files == null) {
            throw new IllegalArgumentException("unable to get root file: " + FileReferenceGenerator.rootPath);
        }
        for (File file : files) {
            if (file.getName().equals(".fileignore")) {
                readFileIgnore(file);
            }
            write(file);
        }
        addLine("}");
        addLine("");
        writeFileReference();
    }

    private static void write(File file) {
        String relativeFilePath = getRelativeFilePath(file.getPath(), rootPath);
        for (String ignoreFile : ignoreFiles) {
            if (relativeFilePath.contains(ignoreFile)) {
                return;
            }
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            assert files != null;
            addLine("public static final String " + file.getName() + "_folder = \"" + relativeFilePath + "/\";");
            addLine("");
            addLine("public static class " + file.getName() + "{");
            for (File childFile : files) {
                write(childFile);
            }
            addLine("}");
        } else {
            String relativeFilePathReference = getRelativeFilePathReference(file.getName(), rootPath);
            addLine("public static final String " + relativeFilePathReference + " = \"" + relativeFilePath + "\";");
        }
    }

    private static void addLine(String newLine) {
        fileReferenceContent.add(lineIndex, newLine);
        lineIndex++;
    }

    private static String getPackageName(String destinationPath) {
        String packageStartPath = "\\src\\";
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

    private static void writeFileReference() {
        try (FileWriter fileWriter = new FileWriter(destinationPath + "/FileReference.java")) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                for (String line : fileReferenceContent) {
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

    private static void readFileIgnore(File file) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath())))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                ignoreFiles.add(line);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}