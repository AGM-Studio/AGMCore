package me.ashenguard.api.utils.extra;

import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.bukkit.util.NumberConversions.*;

@SuppressWarnings("unused")
public class MemorySectionReader {
    protected final MemorySection memory;

    public MemorySectionReader(MemorySection memory) {
        this.memory = memory;
    }
    
    // Objects
    public Object read(String... paths) {
        for (String key: paths) {
            Object value = memory.get(key);
            if (value != null) return value;
        }
        return null;
    }
    public Object read(Object def, String... paths) {
        Object val = read(paths);
        return val == null ? def : val;
    }
    
    // Primitives
    public String readString(String... paths) {
        return readString(null, 0, paths);
    }
    public String readString(String def, int ignored, String... paths) {
        Object val = read(def, paths);
        return (val != null) ? val.toString() : def;
    }
    public boolean isString(String... paths) {
        Object val = read(paths);
        return val instanceof String;
    }

    public int readInt(String... paths) {
        return readInt(0, paths);
    }
    public int readInt(int def, String... paths) {
        Object val = read(def, paths);
        return (val instanceof Number) ? toInt(val) : def;
    }
    public boolean isInt(String... paths) {
        Object val = read(paths);
        return val instanceof Integer;
    }

    public boolean readBoolean(String... paths) {
        return readBoolean(false, paths);
    }
    public boolean readBoolean(boolean def, String... paths) {
        Object val = read(def, paths);
        return (val instanceof Boolean) ? (Boolean) val : def;
    }
    public boolean isBoolean(String... paths) {
        Object val = read(paths);
        return val instanceof Boolean;
    }

    public double readDouble(String... paths) {
        return readDouble(0, paths);
    }
    public double readDouble(double def, String... paths) {
        Object val = read(def, paths);
        return (val instanceof Number) ? toDouble(val) : def;
    }
    public boolean isDouble(String... paths) {
        Object val = read(paths);
        return val instanceof Double;
    }

    public long readLong(String... paths) {
        return readLong(0, paths);
    }
    public long readLong(long def, String... paths) {
        Object val = read(def, paths);
        return (val instanceof Number) ? toLong(val) : def;
    }
    public boolean isLong(String... paths) {
        Object val = read(paths);
        return val instanceof Long;
    }

    // Java
    public List<?> readList(String... paths) {
        return readList(null, paths);
    }
    public List<?> readList(List<?> def, String... paths) {
        Object val = read(def, paths);
        return (List<?>) ((val instanceof List) ? val : def);
    }
    public boolean isList(String... paths) {
        Object val = read(paths);
        return val instanceof List;
    }

    public List<String> readStringList(String... paths) {
        List<?> list = readList(paths);
        if (list == null) {
            return new ArrayList<>(0);
        }

        List<String> result = new ArrayList<>();
        for (Object object : list) {
            if ((object instanceof String) || (isPrimitiveWrapper(object))) {
                result.add(String.valueOf(object));
            }
        }

        return result;
    }

    public List<Integer> readIntegerList(String... paths) {
        List<?> list = readList(paths);
        if (list == null) {
            return new ArrayList<>(0);
        }

        List<Integer> result = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Integer) {
                result.add((Integer) object);
            } else if (object instanceof String) {
                try {
                    result.add(Integer.valueOf((String) object));
                } catch (Exception ignored) {}
            } else if (object instanceof Character) {
                result.add((int) (Character) object);
            } else if (object instanceof Number) {
                result.add(((Number) object).intValue());
            }
        }

        return result;
    }

    public List<Boolean> readBooleanList(String... paths) {
        List<?> list = readList(paths);
        if (list == null) {
            return new ArrayList<>(0);
        }

        List<Boolean> result = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Boolean) {
                result.add((Boolean) object);
            } else if (object instanceof String) {
                if (Boolean.TRUE.toString().equalsIgnoreCase((String) object)) {
                    result.add(true);
                } else if (Boolean.FALSE.toString().equalsIgnoreCase((String) object)) {
                    result.add(false);
                }
            }
        }

        return result;
    }

    public List<Double> readDoubleList(String... paths) {
        List<?> list = readList(paths);
        if (list == null) {
            return new ArrayList<>(0);
        }

        List<Double> result = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Double) {
                result.add((Double) object);
            } else if (object instanceof String) {
                try {
                    result.add(Double.valueOf((String) object));
                } catch (Exception ignored) {}
            } else if (object instanceof Character) {
                result.add((double) (Character) object);
            } else if (object instanceof Number) {
                result.add(((Number) object).doubleValue());
            }
        }

        return result;
    }

    public List<Float> readFloatList(String... paths) {
        List<?> list = readList(paths);
        if (list == null) {
            return new ArrayList<>(0);
        }

        List<Float> result = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Float) {
                result.add((Float) object);
            } else if (object instanceof String) {
                try {
                    result.add(Float.valueOf((String) object));
                } catch (Exception ignored) {}
            } else if (object instanceof Character) {
                result.add((float) (Character) object);
            } else if (object instanceof Number) {
                result.add(((Number) object).floatValue());
            }
        }

        return result;
    }

    public List<Long> readLongList(String... paths) {
        List<?> list = readList(paths);
        if (list == null) {
            return new ArrayList<>(0);
        }

        List<Long> result = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Long) {
                result.add((Long) object);
            } else if (object instanceof String) {
                try {
                    result.add(Long.valueOf((String) object));
                } catch (Exception ignored) {}
            } else if (object instanceof Character) {
                result.add((long) (Character) object);
            } else if (object instanceof Number) {
                result.add(((Number) object).longValue());
            }
        }

        return result;
    }

    public List<Byte> readByteList(String... paths) {
        List<?> list = readList(paths);
        if (list == null) {
            return new ArrayList<>(0);
        }

        List<Byte> result = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Byte) {
                result.add((Byte) object);
            } else if (object instanceof String) {
                try {
                    result.add(Byte.valueOf((String) object));
                } catch (Exception ignored) {
                }
            } else if (object instanceof Character) {
                result.add((byte) ((Character) object).charValue());
            } else if (object instanceof Number) {
                result.add(((Number) object).byteValue());
            }
        }

        return result;
    }

    public List<Character> readCharacterList(String... paths) {
        List<?> list = readList(paths);
        if (list == null) {
            return new ArrayList<>(0);
        }

        List<Character> result = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Character) {
                result.add((Character) object);
            } else if (object instanceof String) {
                String str = (String) object;

                if (str.length() == 1) {
                    result.add(str.charAt(0));
                }
            } else if (object instanceof Number) {
                result.add((char) ((Number) object).intValue());
            }
        }

        return result;
    }

    public List<Short> readShortList(String... paths) {
        List<?> list = readList(paths);
        if (list == null) {
            return new ArrayList<>(0);
        }

        List<Short> result = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Short) {
                result.add((Short) object);
            } else if (object instanceof String) {
                try {
                    result.add(Short.valueOf((String) object));
                } catch (Exception ignored) {}
            } else if (object instanceof Character) {
                result.add((short) ((Character) object).charValue());
            } else if (object instanceof Number) {
                result.add(((Number) object).shortValue());
            }
        }

        return result;
    }
        
    public List<Map<?, ?>> readMapList(String... paths) {
        List<?> list = readList(paths);
        List<Map<?, ?>> result = new ArrayList<>();
        if (list == null) {
            return result;
        }
        for (Object object : list) {
            if (object instanceof Map) {
                result.add((Map<?, ?>) object);
            }
        }

        return result;
    }

    // Bukkit
    public Vector readVector(String... paths) {
        for (String key: paths) {
            Vector value = memory.getVector(key);
            if (value != null) return value;
        }
        return null;
    }
    public Vector readVector(Vector def, String... paths) {
        Vector val = readVector(paths);
        return val == null ? def : val;
    }
    public boolean isVector(String... paths) {
        return readVector(paths) != null;
    }

    public OfflinePlayer readOfflinePlayer(String... paths) {
        for (String key: paths) {
            OfflinePlayer value = memory.getOfflinePlayer(key);
            if (value != null) return value;
        }
        return null;
    }
    public OfflinePlayer readOfflinePlayer(OfflinePlayer def, String... paths) {
        OfflinePlayer val = readOfflinePlayer(paths);
        return val == null ? def : val;
    }
    public boolean isOfflinePlayer(String... paths) {
        return readOfflinePlayer(paths) != null;
    }

    public ItemStack readItemStack(String... paths) {
        for (String key: paths) {
            ItemStack value = memory.getItemStack(key);
            if (value != null) return value;
        }
        return null;
    }
    public ItemStack readItemStack(ItemStack def, String... paths) {
        ItemStack val = readItemStack(paths);
        return val == null ? def : val;
    }
    public boolean isItemStack(String... paths) {
        return readItemStack(paths) != null;
    }

    public Color readColor(String... paths) {
        for (String key: paths) {
            Color value = memory.getColor(key);
            if (value != null) return value;
        }
        return null;
    }
    public Color readColor(Color def, String... paths) {
        Color val = readColor(paths);
        return val == null ? def : val;
    }
    public boolean isColor(String... paths) {
        return readColor(paths) != null;
    }

    public ConfigurationSection readConfigurationSection(String... paths) {
        for (String key: paths) {
            ConfigurationSection value = memory.getConfigurationSection(key);
            if (value != null) return value;
        }
        return null;
    }
    public boolean isConfigurationSection(String... paths) {
        Object val = read(paths);
        return val instanceof ConfigurationSection;
    }

    protected boolean isPrimitiveWrapper(Object input) {
        return input instanceof Integer || input instanceof Boolean ||
                input instanceof Character || input instanceof Byte ||
                input instanceof Short || input instanceof Double ||
                input instanceof Long || input instanceof Float;
    }

    @Override
    public String toString() {
        Configuration root = memory.getRoot();
        return String.format("%s [paths='%s', root='%s']", getClass().getSimpleName(), memory.getCurrentPath(), (root == null ? null : root.getClass().getSimpleName()));
    }
}
