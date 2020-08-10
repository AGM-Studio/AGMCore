package me.ashenguard.api.numeral;

public class AlphabeticNumber {
    static final String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String toAlphabetic(int i, boolean uppercase) {
        if (i < 1) return ""; else i -= 1;
        String alphabets = uppercase ? AlphabeticNumber.alphabets : AlphabeticNumber.alphabets.toLowerCase();
        int base = alphabets.length();

        StringBuilder result = new StringBuilder();
        while (i >= base) {
            result.insert(0, alphabets.charAt(i % base));
            i = i / base - 1;
        }

        return alphabets.charAt(i) + result.toString();
    }

    public static int toInteger(String s) {
        int i = 0;
        s = s.toUpperCase();
        for (int j = 0; j < s.length(); j++) {
            i *= alphabets.length();
            i += alphabets.indexOf(s.charAt(0));
            s = s.substring(1);
        }

        return i;
    }
}
