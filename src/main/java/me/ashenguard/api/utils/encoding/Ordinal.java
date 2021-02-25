package me.ashenguard.api.utils.encoding;

import java.util.Arrays;

@SuppressWarnings("unused")
public class Ordinal {
    public static String to(int i) {
        if (Arrays.asList(11, 12, 13).contains(i % 100)) return i + "th";
        switch (i % 10) {
            case 1: return i + "st";
            case 2: return i + "nd";
            case 3: return i + "rd";
            default: return i + "th";
        }
    }
    public static int from(String string) {
        int index = string.indexOf("st");
        if (index < 0) index = string.indexOf("nd");
        if (index < 0) index = string.indexOf("rd");
        if (index < 0) index = string.indexOf("th");
        return Integer.parseInt(string.substring(0, index));
    }
}