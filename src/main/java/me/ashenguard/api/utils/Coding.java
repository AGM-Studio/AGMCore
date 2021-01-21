package me.ashenguard.api.utils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class Coding {
    public static class Alphabetic {
        static final String ALPHABETS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        public static String to (int i) {
            return to(i, true);
        }
        public static String to(int i, boolean uppercase) {
            if (i < 1) return ""; else i -= 1;
            String alphabets = uppercase ? ALPHABETS : ALPHABETS.toLowerCase();
            int base = alphabets.length();

            StringBuilder result = new StringBuilder();
            while (i >= base) {
                result.insert(0, alphabets.charAt(i % base));
                i = i / base - 1;
            }

            return alphabets.charAt(i) + result.toString();
        }
        public static int from(String s) {
            int i = 0;
            s = s.toUpperCase();
            for (int j = 0; j < s.length(); j++) {
                i *= ALPHABETS.length();
                i += ALPHABETS.indexOf(s.charAt(0));
                s = s.substring(1);
            }

            return i;
        }
    }
    public static class HexCode {
        public static String to(String string) {
            StringBuilder hex = new StringBuilder();
            for (char character: string.toCharArray()) hex.append(Integer.toHexString(character));
            return hex.toString();
        }
        public static String from(String hex) {
            StringBuilder string = new StringBuilder();
            for (int i = 0; i < hex.length(); i+= 2) string.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));
            return string.toString();
        }

        public static String toHidden(String string) {
            StringBuilder code = new StringBuilder();
            for (char character: to(string).toCharArray()) code.append(String.format("§%c", character));
            code.append("§r");
            return code.toString();
        }
        public static String fromHidden(String string) {
            return from(string.replace("§", "").replace("r", ""));
        }
    }
    public static class Ordinal {
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
    public static class Roman {
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
            for(Map.Entry<String, Integer> entry : ROMAN_NUMERALS.entrySet()) {
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
            for(Map.Entry<String, Integer> entry : ROMAN_NUMERALS.entrySet()) {
                while (roman.startsWith(entry.getKey())) {
                    i += entry.getValue();
                    roman = roman.substring(entry.getKey().length());
                }
            }
            return i;
        }
    }
}
