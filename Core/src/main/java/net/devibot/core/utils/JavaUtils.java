package net.devibot.core.utils;

import java.util.ArrayList;
import java.util.List;

public class JavaUtils {

    public static <T> List<List<T>> chopList(List<T> list, int size) {
        List<List<T>> parts = new ArrayList<>();
        int listSize = list.size();
        for (int i = 0; i < listSize; i += size) {
            parts.add(new ArrayList<>(list.subList(i, Math.min(listSize, i + size))));
        }
        return parts;
    }

    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) {
            longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

    }

    private static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public static Boolean getBoolean(String input) {
        if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("on")) return true;
        if (input.equalsIgnoreCase("false") || input.equalsIgnoreCase("off")) return false;
        return null;
    }

    public static String makeBooleanBeautiful(boolean bool) {
        return bool ? "on" : "off";
    }

    public static void notNull(Object argument, String name) {
        if (argument == null) {
            throw new IllegalArgumentException(name + " may not be null");
        }
    }
}
