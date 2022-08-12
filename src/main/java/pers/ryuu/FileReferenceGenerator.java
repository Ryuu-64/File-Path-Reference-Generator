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
        generate("E:\\Air-Hockey\\assets\\", "E:\\Air-Hockey\\core\\src\\com\\coolstudios\\air_hockey\\", "com.coolstudios.air_hockey");
    }

    private static final ArrayList<String> fileReferenceContent = new ArrayList<>(1 << 10);
    private static final ArrayList<String> ignorePatterns = new ArrayList<>(1 << 5);
    private static final ArrayList<String> notIgnorePatterns = new ArrayList<>(1 << 5);
    private static String filePath;
    private static String writePath;
    private static int lineIndex = -1;
    private static int depth = 1;

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
                continue;
            }
            write(file);
        }
        addLine("}");
        addLine("");
        writeFileReference();
    }

    private static void write(File file) {
        String relativeFilePath = getRelativeFilePath(file, filePath);
        boolean isIgnore = isIgnore(relativeFilePath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            assert files != null;
            if (!isIgnore) {
                if (fileReferenceContent.get(lineIndex).endsWith("}")) {
                    addLine("", depth);
                }
                String folderString = "public static final String " + file.getName() + "$folder = \"" + relativeFilePath + "\";";
                addLine(folderString, depth);
            }
            addLine("", depth);
            String classString = "public static class " + file.getName() + " {";
            addLine(classString, depth);
            depth++;
            for (File childFile : files) {
                write(childFile);
            }
            depth--;
            addLine("}", depth);

            // if the static class contains no field remove the class
            if (fileReferenceContent.get(lineIndex - 1).endsWith(classString) && fileReferenceContent.get(lineIndex).endsWith("}")) {
                removeLine();
                removeLine();
            }
        } else {
            if (!isIgnore) {
                if (fileReferenceContent.get(lineIndex).endsWith("}")) {
                    addLine("", depth);
                }
                addLine("public static final String " + getFileFieldName(file.getName()) + " = \"" + relativeFilePath + "\";", depth);
            }
        }
    }

    private static boolean isIgnore(String path) {
        boolean isIgnore = false;
        for (String pattern : ignorePatterns) {
            if (Pattern.matches(pattern, path)) {
                isIgnore = true;
            }
        }

        if (isIgnore) {
            for (String pattern : notIgnorePatterns) {
                if (Pattern.matches(pattern, path)) {
                    isIgnore = false;
                    break;
                }
            }
        }
        return isIgnore;
    }

    private static void addLine(String newLine, int tabCount) {
        lineIndex++;
        StringBuilder builder = new StringBuilder(newLine);
        while (tabCount > 0) {
            builder.insert(0, "\t");
            tabCount--;
        }
        newLine = builder.toString();
        fileReferenceContent.add(lineIndex, newLine);
    }

    private static void addLine(String newLine) {
        lineIndex++;
        fileReferenceContent.add(lineIndex, newLine);
    }

    private static void removeLine() {
        fileReferenceContent.remove(lineIndex);
        lineIndex--;
    }

    private static String getRelativeFilePath(File file, String prefix) {
        String path = file.getPath();
        path = path.replace(prefix, "");
        path = path.replace('\\', '/');
        if (file.isDirectory()) {
            path = path + "/";
        } else {
            path = dealWithIllegalFieldName(path);
        }
        return path;
    }

    private static String getFileFieldName(String path) {
        path = path.replace(' ', '_');
        path = path.replace('-', '_');
        path = path.replace('.', '_');
        path = path.replace('/', '_');
        path = path.replace('\\', '_');
        path = dealWithIllegalFieldName(path);
        return path;
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

    private static String dealWithIllegalFieldName(String fileName) {
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
                if (line.equals("") || line.startsWith("#")) {
                    continue;
                }

                if (line.startsWith("!")) {
                    line = line.replaceFirst("!", "");
                    if (line.contains("*")) {
                        line = line.replace("*", ".*");
                    }
                    line = ".*" + line;
                    notIgnorePatterns.add(line);
                } else {
                    if (line.contains("*")) {
                        line = line.replace("*", ".*");
                    }
                    line = ".*" + line;
                    ignorePatterns.add(line);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}