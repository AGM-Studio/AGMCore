package me.ashenguard.api.utils.encoding;

@SuppressWarnings("unused")
public class Alphabetic {
    static final String ALPHABETS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String to(int i) {
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