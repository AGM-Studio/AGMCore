package me.ashenguard.api.numeral;

import java.util.Arrays;

public class OrdinalInteger {
    public static String toOrdinal(int i) {
        if (Arrays.asList(11, 12, 13).contains(i % 100)) return i + "th";
        switch (i % 10) {
            case 1: return i + "st";
            case 2: return i + "nd";
            case 3: return i + "rd";
            default: return i + "th";
        }
    }
}
