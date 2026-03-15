package com.civicfix.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class SeverityService {

    private static final List<String> CRITICAL_KEYWORDS = Arrays.asList(
            "collapse", "flood", "fire", "danger", "accident", "emergency", "electric shock",
            "sewage overflow", "burst pipe", "gas leak", "hazard", "unsafe", "injury",
            "blocked road"
    );

    private static final List<String> HIGH_KEYWORDS = Arrays.asList(
            "pothole", "broken", "no water", "no electricity", "overflow", "damaged",
            "large", "deep", "major", "serious", "urgent", "leak"
    );

    private static final List<String> LOW_KEYWORDS = Arrays.asList(
            "minor", "small", "slight", "cosmetic", "paint", "sign", "bench",
            "graffiti", "noise"
    );

    private static final List<String> HIGH_CATEGORIES = Arrays.asList(
            "pothole", "water leak", "sewage"
    );

    private static final List<String> MEDIUM_CATEGORIES = Arrays.asList(
            "streetlight", "garbage"
    );

    /**
     * Calculate severity based on keywords in category and description.
     * Critical > High > Medium (default by category) > Low.
     */
    public String calculateSeverity(String category, String description) {
        if (category == null) category = "";
        if (description == null) description = "";

        String combined = (category + " " + description).toLowerCase(Locale.ROOT);
        String catLower = category.trim().toLowerCase(Locale.ROOT);

        for (String keyword : CRITICAL_KEYWORDS) {
            if (combined.contains(keyword)) {
                return "Critical";
            }
        }

        for (String keyword : HIGH_KEYWORDS) {
            if (combined.contains(keyword)) {
                return "High";
            }
        }

        for (String keyword : LOW_KEYWORDS) {
            if (combined.contains(keyword)) {
                return "Low";
            }
        }

        for (String c : HIGH_CATEGORIES) {
            if (catLower.contains(c)) {
                return "High";
            }
        }

        for (String c : MEDIUM_CATEGORIES) {
            if (catLower.contains(c)) {
                return "Medium";
            }
        }

        return "Medium";
    }
}
