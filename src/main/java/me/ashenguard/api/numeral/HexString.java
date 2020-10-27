package me.ashenguard.api.numeral;

public class HexString {
    public static String toHex(String string) {
        StringBuilder hex = new StringBuilder();
        for (char character: string.toCharArray()) hex.append(Integer.toHexString(character));
        return hex.toString();
    }

    public static String fromHex(String hex) {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < hex.length(); i+= 2) string.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));
        return string.toString();
    }
}
