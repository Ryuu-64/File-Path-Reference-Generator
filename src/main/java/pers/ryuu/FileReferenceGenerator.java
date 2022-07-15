package pers.ryuu;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileReferenceGenerator {
    // TODO update readme
    // TODO .fileignore regex
    public static void main(String[] args) {
        generate(
                "E:\\LibgdxWorkSpace\\Air-Hockey\\assets",
                "E:\\LibgdxWorkSpace\\Air-Hockey\\core\\src\\com\\coolstudios\\airhockey",
                "com.coolstudios.airhockey"
        );
    }

    private static final ArrayList<String> fileReferenceContent = new ArrayList<>(1 << 10);
    private static final ArrayList<String> ignoreFiles = new ArrayList<>(1 << 5);
    private static String filePath;
    private static String writePath;
    private static int lineIndex = 0;

    public static void generate(String filePath, String writePath, String packageName) {
        FileReferenceGenerator.filePath = dealInputFolderPath(filePath);
        FileReferenceGenerator.writePath = dealInputFolderPath(writePath);
        addLine("package " + packageName + ";");
        addLine("");
        addLine("public class FileReference {");
        File rootFile = new File(FileReferenceGenerator.filePath);
        File[] files = rootFile.listFiles();
        if (files == null) {
            throw new IllegalArgumentException("unable to get root file: " + FileReferenceGenerator.filePath);
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
        String relativeFilePath = getRelativeFilePath(file.getPath(), filePath);
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
            // TODO calculate write childFile count
            for (File childFile : files) {
                write(childFile);
            }
            addLine("}");
        } else {
            addLine("public static final String " + getFileFieldName(file.getName()) + " = \"" + relativeFilePath + "\";");
        }
    }

    private static void addLine(String newLine) {
        fileReferenceContent.add(lineIndex, newLine);
        lineIndex++;
    }

    private static String getRelativeFilePath(String path, String prefix) {
        path = path.replace(prefix, "");
        path = path.replace('\\', '/');
        return dealStartWithNumber(path);
    }

    private static String getFileFieldName(String path) {
        path = path.replace(' ', '_');
        path = path.replace('-', '_');
        path = path.replace('.', '_');
        path = path.replace('/', '_');
        path = path.replace('\\', '_');
        return dealStartWithNumber(path);
    }

    private static void writeFileReference() {
        try (FileWriter fileWriter = new FileWriter(writePath + "/FileReference.java")) {
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
                if (line.equals("")) {
                    continue;
                }
                ignoreFiles.add(line);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}