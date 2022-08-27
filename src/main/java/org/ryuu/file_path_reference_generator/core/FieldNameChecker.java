package org.ryuu.file_path_reference_generator.core;

import java.util.HashSet;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

public class FieldNameChecker {
    private FieldNameChecker() {
    }

    private static final HashSet<String> excludeIdentifiers = new HashSet<String>() {{
        add("abstract");
        add("assert");
        add("boolean");
        add("break");
        add("byte");
        add("case");
        add("catch");
        add("char");
        add("class");
        add("const");
        add("continue");
        add("default");
        add("do");
        add("double");
        add("else");
        add("enum");
        add("extends");
        add("final");
        add("finally");
        add("float");
        add("for");
        add("goto");
        add("if");
        add("implements");
        add("import");
        add("instanceof");
        add("int");
        add("interface");
        add("long");
        add("native");
        add("new");
        add("package");
        add("private");
        add("protected");
        add("public");
        add("return");
        add("short");
        add("static");
        add("strictfp");
        add("super");
        add("switch");
        add("synchronized");
        add("this");
        add("throw");
        add("throws");
        add("transient");
        add("try");
        add("void");
        add("volatile");
        add("while");
        add("true");
        add("false");
        add("null");
    }};

    public static String getLegal(String name) {
        if (excludeIdentifiers.contains(name)) {
            return "$" + name;
        }
        String legalName = name.replaceAll("[^\\da-zA-Z_$]", "_");
        if (matches("^[^a-zA-Z_$].*$", legalName)) {
            return "$" + legalName;
        } else {
            return legalName;
        }
    }
}