package com.example.madiotech;

public final class StringUtils {
    private StringUtils() {}

    public static String safeTrim(String s) {
        return (s == null) ? "" : s.trim();
    }
}

