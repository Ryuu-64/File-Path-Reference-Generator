package org.ryuu.pathgenerator;

import org.junit.jupiter.api.Test;

import javax.lang.model.SourceVersion;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class IdentifierProcessorTest {

    @Test
    void getLegal() {
        HashSet<String> illegalIdentifiers = new HashSet<String>() {{
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

        for (String identifiers : illegalIdentifiers) {
            System.out.println(SourceVersion.isKeyword("$" + identifiers));
        }
    }
}