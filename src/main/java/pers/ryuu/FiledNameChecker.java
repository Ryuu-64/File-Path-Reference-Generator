package pers.ryuu;

import java.util.HashSet;
import java.util.regex.Pattern;

public class FiledNameChecker {
    private FiledNameChecker() {
    }

    private static final HashSet<String> illegalFieldNameSet = new HashSet<String>() {{
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

    public static boolean isLegal(String fieldName) {
        if (illegalFieldNameSet.contains(fieldName)) {
            return false;
        }
        String regex = "^[a-zA-Z_$][\\da-zA-Z_$]*$";
        return Pattern.matches(regex, fieldName);
    }
}
