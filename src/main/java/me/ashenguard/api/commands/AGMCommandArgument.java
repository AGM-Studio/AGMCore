package me.ashenguard.api.commands;

import me.ashenguard.AGMConstants;
import me.ashenguard.api.commands.annotations.DefaultValue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

public class AGMCommandArgument {
    private final Class<?> cls;
    private final Object def;

    private final Function<String, Object> caster;
    private final Function<String, List<String>> recommender;


    protected static AGMCommandArgument from(Class<?> cls) {
        return new AGMCommandArgument(cls);
    }

    private AGMCommandArgument(Class<?> cls) {
        this.cls = cls;

        this.caster = getCaster();
        this.recommender = getRecommender();

        this.def = cls.isAnnotationPresent(DefaultValue.class) ? this.caster.apply(cls.getAnnotation(DefaultValue.class).value()) : null;
    }

    private static Function<String, List<String>> dynamicRecommender(final String from) {
        return s -> from.chars().mapToObj(i -> s + (char) i).toList();
    }
    @SuppressWarnings("SameParameterValue")
    private static Function<String, List<String>> dynamicRecommender(Predicate<String> condition, final String from1, final String from2) {
        return s -> {
            if (condition.test(s)) return from1.chars().mapToObj(i -> s + (char) i).toList();
            else return from2.chars().mapToObj(i -> s + (char) i).toList();
        };
    }

    private Function<String, List<String>> getRecommender() {
        if (cls == byte.class || cls == Byte.class)
            return dynamicRecommender("01");
        if (cls == short.class || cls == Short.class || cls == int.class || cls == Integer.class || cls == long.class || cls == Long.class)
            return dynamicRecommender(AGMConstants.NUMBERS);
        if (cls == float.class || cls == Float.class || cls == double.class || cls == Double.class)
            return dynamicRecommender(s -> s.contains("."), AGMConstants.NUMBERS, AGMConstants.NUMBERS + ".");
        if (cls == boolean.class || cls == Boolean.class)
            return s -> Arrays.asList("true", "false");
        if (cls == char.class || cls == Character.class)
            return s -> AGMConstants.LITERAL.chars().mapToObj(i -> String.valueOf((char) i)).toList();

        if (cls == Player.class) return s -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();

        return Collections::singletonList;
    }

    private Function<String, Object> getCaster() {
        if (cls == byte.class || cls == Byte.class) return s -> {
            try {
                return Byte.parseByte(s);
            } catch (NumberFormatException ignored) {
                return getDefault();
            }
        };
        if (cls == short.class || cls == Short.class) return s -> {
            try {
                return Short.parseShort(s);
            } catch (NumberFormatException ignored) {
                return getDefault();
            }
        };
        if (cls == int.class || cls == Integer.class) return s -> {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException ignored) {
                return getDefault();
            }
        };
        if (cls == long.class || cls == Long.class) return s -> {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException ignored) {
                return getDefault();
            }
        };
        if (cls == float.class || cls == Float.class) return s -> {
            try {
                return Float.parseFloat(s);
            } catch (NumberFormatException ignored) {
                return getDefault();
            }
        };
        if (cls == double.class || cls == Double.class) return s -> {
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException ignored) {
                return getDefault();
            }
        };
        if (cls == boolean.class || cls == Boolean.class) return s -> {
            try {
                return Boolean.parseBoolean(s);
            } catch (NumberFormatException ignored) {
                return getDefault();
            }
        };
        if (cls == char.class || cls == Character.class) return s -> {
            try {
                return s.charAt(0);
            } catch (NumberFormatException ignored) {
                return getDefault();
            }
        };

        if (cls == Player.class) return s -> {
            try {
                return Bukkit.getPlayer(UUID.fromString(s));
            } catch (IllegalArgumentException ignored) {
                return Bukkit.getPlayer(s);
            }
        };

        return cls::cast;
    }

    public Object getDefault() {
        if (def != null) return def;

        if (cls == byte.class || cls == short.class || cls == int.class || cls == long.class) return cls.cast(0);
        if (cls == float.class || cls == double.class) return cls.cast(0.0);
        if (cls == char.class) return (char) 0;
        if (cls == boolean.class) return false;

        return null;
    }

    public Object cast(String arg) {
        if (arg == null) return getDefault();
        try {
            Object obj = caster.apply(arg);
            return obj == null ? getDefault() : obj;
        } catch (Throwable ignored) {
            return getDefault();
        }
    }

    public List<String> recommend(String arg) {
        return recommender.apply(arg);
    }
}
