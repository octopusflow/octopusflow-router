package com.octopusflow.util;

import org.junit.Test;

public class UtilTest {

    @Test
    public void testIsValidRegex() {
        assert !Util.isValidRegex("\\w+.");
        assert !Util.isValidRegex("db.table");
        assert Util.isValidRegex("r'\\w+.\\w'");
    }
}
