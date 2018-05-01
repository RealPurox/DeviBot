package me.purox.devi.utils;

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

    public static Boolean getBoolean(String input) {
        if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("on")) return true;
        if (input.equalsIgnoreCase("false") || input.equalsIgnoreCase("off")) return false;
        return null;
    }

    public static String makeBooleanBeautiful(boolean bool) {
        return bool ? "on" : "off";
    }
}
