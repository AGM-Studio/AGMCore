package me.ashenguard.api.commands;

import me.ashenguard.AGMConstants;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public enum AGMCommandArgument {
    PLAYER(
            s -> Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toList(),
            Bukkit::getPlayer,
            Player.class
    ),
    STRING(
            s -> AGMConstants.LITERAL.chars().mapToObj(i -> s + (char) i).toList(),
            s -> s,
            String.class
    ),
    INTEGER(
            s -> AGMConstants.NUMBERS.chars().mapToObj(i -> s + (char) i).toList(),
            s -> {
                try {
                    return Integer.parseInt(s);
                } catch (NumberFormatException exception) {
                    throw new AGMCommandException(String.format("Unable to cast \"%s\" argument to an integer", s), exception, true);
                }
            },
            int.class
    ),
    INTEGER_NULLABLE(
            s -> AGMConstants.NUMBERS.chars().mapToObj(i -> s + (char) i).toList(),
            s -> {
                try {
                    return Integer.parseInt(s);
                } catch (NumberFormatException exception) {
                    return null;
                }
            },
            Integer.class
    ),
    DOUBLE(
            s -> {
                if (s.contains(".")) return AGMConstants.NUMBERS.chars().mapToObj(i -> s + (char) i).toList();
                return (AGMConstants.NUMBERS + ".").chars().mapToObj(i -> s + (char) i).toList();
            },
            s -> {
                try {
                    return Double.parseDouble(s);
                } catch (NumberFormatException exception) {
                    throw new AGMCommandException(String.format("Unable to cast \"%s\" argument to a double", s), exception, true);
                }
            },
            double.class
    ),
    DOUBLE_NULLABLE(
            s -> {
                if (s.contains(".")) return AGMConstants.NUMBERS.chars().mapToObj(i -> s + (char) i).toList();
                return (AGMConstants.NUMBERS + ".").chars().mapToObj(i -> s + (char) i).toList();
            },
            s -> {
                try {
                    return Double.parseDouble(s);
                } catch (NumberFormatException exception) {
                    return null;
                }
            },
            Double.class
    ),
    FLOAT(
            s -> {
                if (s.contains(".")) return AGMConstants.NUMBERS.chars().mapToObj(i -> s + (char) i).toList();
                return (AGMConstants.NUMBERS + ".").chars().mapToObj(i -> s + (char) i).toList();
            },
            s -> {
                try {
                    return Float.parseFloat(s);
                } catch (NumberFormatException exception) {
                    throw new AGMCommandException(String.format("Unable to cast \"%s\" argument to a float", s), exception, true);
                }
            },
            float.class
    ),
    FLOAT_NULLABLE(
            s -> {
                if (s.contains(".")) return AGMConstants.NUMBERS.chars().mapToObj(i -> s + (char) i).toList();
                return (AGMConstants.NUMBERS + ".").chars().mapToObj(i -> s + (char) i).toList();
            },
            s -> {
                try {
                    return Float.parseFloat(s);
                } catch (NumberFormatException exception) {
                    return null;
                }
            },
            Float.class
    ),
    OTHERS(Collections::singletonList, s -> s, String.class);

    private final Function<String, List<String>> tabCompleter;
    private final Function<String, Object> caster;
    private final Class<?> cls;

    <T> AGMCommandArgument(Function<String, List<String>> tabCompleter, Function<String, T> caster, Class<T> cls) {
        this.tabCompleter = tabCompleter;
        this.caster = caster::apply;
        this.cls = cls;
    }

    public List<String> getTab(String arg) {
        return tabCompleter.apply(arg);
    }

    public Object cast(String arg) {
        return caster.apply(arg);
    }

    public static AGMCommandArgument getType(Class<?> cls) {
        for (AGMCommandArgument argument : values()) if (argument.cls == cls) return argument;
        return OTHERS;
    }
}
