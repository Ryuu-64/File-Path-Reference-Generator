package org.ryuu.file_reference.gui;

import java.util.prefs.Preferences;

public class Persistence {
    private static final Preferences preferences = Preferences.userNodeForPackage(GUI.class);

    public static String getRootFilePath() {
        return preferences.get("rootFilePath", "");
    }

    public static String getReferencePath() {
        return preferences.get("referencePath", "");
    }

    public static String getPackageName() {
        return preferences.get("packageName", "");
    }

    public static String getScriptName() {
        return preferences.get("scriptName", "FileReference.java");
    }

    public static void putRootFilePath(String path) {
        preferences.put("rootFilePath", path);
    }

    public static void putReferencePath(String path) {
        preferences.put("referencePath", path);
    }

    public static void putPackageName(String name) {
        preferences.put("packageName", name);
    }

    public static void putScriptName(String name) {
        preferences.put("scriptName", name);
    }
}