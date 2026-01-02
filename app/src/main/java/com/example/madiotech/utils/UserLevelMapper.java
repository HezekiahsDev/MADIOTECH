package com.example.madiotech.utils;

public final class UserLevelMapper {
    private UserLevelMapper() {}

    public static String mapUserLevel(String level) {
        if (level == null) return "new user";
        switch (level.trim()) {
            case "1":
                return "Basic user";
            case "2":
                return "Basic user";
            case "3":
                return "Reseller";
            case "4":
                return "Partner";
            case "5":
                return "Premium";
            default:
                String lower = level.toLowerCase();
                if (lower.contains("new") || lower.contains("basic") || lower.contains("reseller") || lower.contains("partner") || lower.contains("premium")) {
                    return level;
                }
                return "new user";
        }
    }
}

