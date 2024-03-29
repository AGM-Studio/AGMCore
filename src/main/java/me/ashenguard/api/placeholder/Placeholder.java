package me.ashenguard.api.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Placeholder {
    private final Predicate<String> validator;
    private final Function<String, String> extractName;
    private final BiFunction<OfflinePlayer, String, String> value;

    private static Predicate<String> equality(String string) {
        return (String name) -> name.equals(string);
    }

    public Placeholder(PlaceholderExtension extension, String identifier, BiFunction<OfflinePlayer, String, String> value) {
        this(extension, equality(identifier), (String string) -> identifier, value);
    }

    public Placeholder(PlaceholderExtension extension, Predicate<String> validator, Function<String, String> extractName, BiFunction<OfflinePlayer, String, String> value) {
        this(validator, extractName, value);
        extension.addPlaceholder(this);
    }

    public Placeholder(String identifier, BiFunction<OfflinePlayer, String, String> value) {
        this(equality(identifier), (String string) -> identifier, value);
    }

    public Placeholder(Predicate<String> validator, Function<String, String> extractName, BiFunction<OfflinePlayer, String, String> value) {
        this.validator = validator;
        this.extractName = extractName;
        this.value = value;
    }

    public boolean isValid(String identifier) {
        return validator.test(identifier);
    }
    public String getValue(OfflinePlayer player, String identifier) {
        return value.apply(player, extractName.apply(identifier));
    }

    public String apply(String string, OfflinePlayer player) {
        if (value == null) return string;

        List<String> placeholders = new ArrayList<>();
        Matcher matcher = Pattern.compile("%(\\w+?)%").matcher(string);
        while (matcher.find()) placeholders.add(matcher.group(1));
        List<String> brackets = new ArrayList<>();
        Matcher bracketMatcher = Pattern.compile("\\{(\\w+?)}").matcher(string);
        while (bracketMatcher.find()) brackets.add(bracketMatcher.group(1));

        for(String placeholder: placeholders) {
            if (isValid(placeholder)) {
                String value = getValue(player, placeholder);
                string = string.replaceFirst(String.format("%%%s%%", placeholder), value);
            }
        }
        for(String bracket: brackets) {
            if (isValid(bracket)) {
                String value = getValue(player, bracket);
                string = string.replaceFirst(String.format("{%s}", bracket), value);
            }
        }
        return string;
    }

    public static String apply(String string, Player player, Placeholder... placeholders) {
        for (Placeholder placeholder: placeholders) string = placeholder.apply(string, player);
        return string;
    }
}
