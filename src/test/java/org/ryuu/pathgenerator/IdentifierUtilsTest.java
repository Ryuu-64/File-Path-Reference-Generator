package org.ryuu.pathgenerator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.lang.model.SourceVersion;
import java.util.HashSet;

class IdentifierUtilsTest {

    @Test
    void getLegal() {
        HashSet<String> reservedWords = new HashSet<String>() {{
            add("abstract");
            add("assert");
            add("boolean");
            add("break");
            add("byte");
            add("case");
            add("catch");
            add("char");
            add("class");
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
            add("const");
            add("goto");
            add("false");
            add("null");
            add("true");
        }};

        for (String reservedWord : reservedWords) {
            boolean isKeyword = SourceVersion.isKeyword(reservedWord);
            Assertions.assertTrue(isKeyword);
        }
    }
}