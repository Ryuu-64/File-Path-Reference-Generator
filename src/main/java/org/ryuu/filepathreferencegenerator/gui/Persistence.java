package org.ryuu.filepathreferencegenerator.gui;

import org.ryuu.filepathreferencegenerator.core.Generator;

import java.util.prefs.Preferences;

public class Persistence {
    private Persistence() {
    }

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
        return preferences.get("scriptName", Generator.DEFAULT_REFERENCE_SCRIPT_NAME);
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