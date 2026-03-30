package com.cdl.epms.util;

public class GoalCategoryUtil {

    public static int mapPotential(String potential) {
        if (potential == null) return 0;

        return switch (potential.toLowerCase()) {
            case "high" -> 3;
            case "medium" -> 2;
            case "low" -> 1;
            default -> 0;
        };
    }

    public static int mapPerformance(String performance) {
        if (performance == null) return 0;

        return switch (performance.toLowerCase()) {
            case "high" -> 3;
            case "average" -> 2;
            case "low" -> 1;
            default -> 0;
        };
    }

    public static String calculateCategory(String potential, String performance) {
        int p = mapPotential(potential);
        int perf = mapPerformance(performance);

        if (p == 3 && perf == 3) return "Star";
        if (p == 3 && perf == 2) return "High Performer";
        if (p == 3 && perf == 1) return "High Potential";

        if (p == 2 && perf == 3) return "Potential Gem";
        if (p == 2 && perf == 2) return "Core Player";
        if (p == 2 && perf == 1) return "Solid Performer";

        if (p == 1 && perf == 3) return "Inconsistent Player";
        if (p == 1 && perf == 2) return "Average Performer";
        if (p == 1 && perf == 1) return "Risk";

        return "Uncategorized";
    }
}