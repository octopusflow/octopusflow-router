package com.octopusflow.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Util {
    private static final Logger log = LoggerFactory.getLogger(Util.class);

    /**
     * determine if string s is regex string, r'\w+', be careful with r'' mode!
     * @param s raw string
     * @return true if it is valid regex
     */
    public static boolean isValidRegex(String s) {
        if (s == null || s.isEmpty() || s.length() < "r''".length() || !s.startsWith("r'") || !s.endsWith("'")) {
            return false;
        }
        String pattern = s.substring(2, s.length()-1);;
        try {
            Pattern.compile(pattern);
            log.info("{} is valid regex", s);
        } catch (PatternSyntaxException ex) {
            log.info("{} is not valid regex", s);
            return false;
        }
        return true;
    }
}
