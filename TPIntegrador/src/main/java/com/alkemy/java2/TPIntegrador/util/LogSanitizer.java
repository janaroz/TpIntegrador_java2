package com.alkemy.java2.TPIntegrador.util;

public class LogSanitizer {
    public static String sanitize(String input) {
        if (input == null) return null;
        return input.replaceAll("[\n\r]", "_");
    }
}
