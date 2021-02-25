package me.ashenguard.api.utils.encoding;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class Roman {
    private static final LinkedHashMap<String, Integer> ROMAN_NUMERALS = new LinkedHashMap<>();

    static {
        ROMAN_NUMERALS.put("M", 1000);
        ROMAN_NUMERALS.put("CM", 900);
        ROMAN_NUMERALS.put("D", 500);
        ROMAN_NUMERALS.put("CD", 400);
        ROMAN_NUMERALS.put("C", 100);
        ROMAN_NUMERALS.put("XC", 90);
        ROMAN_NUMERALS.put("L", 50);
        ROMAN_NUMERALS.put("XL", 40);
        ROMAN_NUMERALS.put("X", 10);
        ROMAN_NUMERALS.put("IX", 9);
        ROMAN_NUMERALS.put("V", 5);
        ROMAN_NUMERALS.put("IV", 4);
        ROMAN_NUMERALS.put("I", 1);
    }

    public static String to(int i) {
        StringBuilder roman = new StringBuilder();
        for (Map.Entry<String, Integer> entry : ROMAN_NUMERALS.entrySet()) {
            while (i >= entry.getValue()) {
                roman.append(entry.getKey());
                i -= entry.getValue();
            }
        }
        return roman.toString();
    }

    public static String to(int i, boolean uppercase) {
        return uppercase ? to(i) : to(i).toLowerCase();
    }

    public static int from(String roman) {
        roman = roman.replace(" ", "");
        int i = 0;
        for (Map.Entry<String, Integer> entry : ROMAN_NUMERALS.entrySet()) {
            while (roman.startsWith(entry.getKey())) {
                i += entry.getValue();
                roman = roman.substring(entry.getKey().length());
            }
        }
        return i;
    }
}
