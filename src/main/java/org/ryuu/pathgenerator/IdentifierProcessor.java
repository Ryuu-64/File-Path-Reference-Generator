package org.ryuu.pathgenerator;

import javax.lang.model.SourceVersion;
import java.util.regex.Pattern;

public class IdentifierProcessor {
    private static final Pattern illegalPrefixCharacter = Pattern.compile("^[^a-zA-Z_$].*$");
    private static final Pattern illegalCharacter = Pattern.compile("[^\\da-zA-Z_$]");

    private IdentifierProcessor() {
    }

    public static String getLegal(String identifier) {
        if (SourceVersion.isKeyword(identifier)) {
            return "$" + identifier;
        }

        identifier = illegalCharacter.matcher(identifier).replaceAll("_");
        boolean isPrefixIllegal = illegalPrefixCharacter.matcher(identifier).matches();
        return isPrefixIllegal ? "$" + identifier : identifier;
    }
}