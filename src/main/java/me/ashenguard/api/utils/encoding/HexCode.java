package me.ashenguard.api.utils.encoding;

@SuppressWarnings("unused")
public class HexCode {
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
