package me.ashenguard.api;

import java.util.LinkedHashMap;
import java.util.Map;

public class RomanInteger {
    private static LinkedHashMap<String, Integer> roman_numerals = new LinkedHashMap<>();
    static {
        roman_numerals.put("M", 1000);
        roman_numerals.put("CM", 900);
        roman_numerals.put("D", 500);
        roman_numerals.put("CD", 400);
        roman_numerals.put("C", 100);
        roman_numerals.put("XC", 90);
        roman_numerals.put("L", 50);
        roman_numerals.put("XL", 40);
        roman_numerals.put("X", 10);
        roman_numerals.put("IX", 9);
        roman_numerals.put("V", 5);
        roman_numerals.put("IV", 4);
        roman_numerals.put("I", 1);
    }

    public static String toRoman(int i) {
        StringBuilder roman = new StringBuilder();
        for(Map.Entry<String, Integer> entry : roman_numerals.entrySet()) {
            while (i >= entry.getValue()) {
                roman.append(entry.getKey());
                i -= entry.getValue();
            }
        }
        return roman.toString();
    }

    public static int toInteger(String roman) {
        roman = roman.replace(" ", "");
        int i = 0;
        for(Map.Entry<String, Integer> entry : roman_numerals.entrySet()) {
            while (roman.startsWith(entry.getKey())) {
                i += entry.getValue();
                roman = roman.substring(entry.getKey().length());
            }
        }
        return i;
    }
}
