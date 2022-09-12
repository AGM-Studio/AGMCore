package me.ashenguard.api.gui;

import me.ashenguard.agmcore.AGMCore;
import me.ashenguard.api.Configuration;
import me.ashenguard.api.itemstack.placeholder.PlaceholderItemStack;
import me.ashenguard.api.placeholder.Placeholder;
import me.ashenguard.api.utils.Pair;
import me.ashenguard.reflected.ReflectedField;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class GUIInventory {
    private static final Pattern MULTI_ITEM_PATTERN = Pattern.compile("^(\\d+)x(.*?)$");
    private static final Pattern SLOT_OFFSET_PATTERN = Pattern.compile("^(\\d+)-(\\d+)$");
    private static final Pattern SLOT_PATTERN = Pattern.compile("^(\\d+)$");

    private static final Map<Player, GUIInventory> INVENTORY_MAP = new HashMap<>();
    public static Map<Player, GUIInventory> getInventoryMap() {
        return INVENTORY_MAP;
    }

    private boolean loaded = false;

    protected final String title;
    protected final int size;

    protected Map<String, PlaceholderItemStack> ITEM_MAP = new HashMap<>();
    protected Map<Integer, GUIInventorySlot> SLOT_MAP = new HashMap<>();

    protected final Set<Placeholder> placeholders = new HashSet<>();

    private @NotNull Pair<PlaceholderItemStack, Integer> getItem(String key) {
        Matcher MIM = MULTI_ITEM_PATTERN.matcher(key);
        boolean MIMFound = MIM.find();
        if (MIMFound) key = MIM.group(2);

        String local = key.toLowerCase().startsWith("inventory:") ? key.substring(10) : key;
        PlaceholderItemStack item = ITEM_MAP.getOrDefault(local.toLowerCase(), null);
        if (item != null) return new Pair<>(item, MIMFound ? Integer.parseInt(MIM.group(1)) : 1);

        return new Pair<>(AGMCore.getItemLibrary().getNotNullItem(key), MIMFound ? Integer.parseInt(MIM.group(1)) : 1);
    }
    private static Pair<Integer, Integer> getSlotAndOffset(String string) {
        Matcher SOM = SLOT_OFFSET_PATTERN.matcher(string);
        Matcher SM = SLOT_PATTERN.matcher(string);
        if (SOM.find()) return new Pair<>(Integer.parseInt(SOM.group(1)), Integer.parseInt(SOM.group(2)));
        else if (SM.find()) return new Pair<>(Integer.parseInt(SM.group(1)), 0);
        else return null;
    }

    private void loadItems(ConfigurationSection section) {
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            if (section.isConfigurationSection(key)) {
                PlaceholderItemStack item = PlaceholderItemStack.fromSection(section.getConfigurationSection(key));
                if (item != null) ITEM_MAP.put(key.toLowerCase(), item);
            }
        }
    }
    private void loadSlots(ConfigurationSection section) {
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            List<String> slots = section.getStringList(String.format("%s.Slots", key));
            List<String> itemList = section.getStringList(String.format("%s.Items", key));
            List<String> altList = section.getStringList(String.format("%s.AltItems", key));

            for (String slot : slots) {
                Pair<Integer, Integer> sop = getSlotAndOffset(slot);
                if (sop == null) continue;

                GUIInventorySlot inventorySlot = new GUIInventorySlot(key, sop.getKey());
                inventorySlot.setItems(itemList.stream().map(this::getItem).toList()).withOffset(sop.getValue());
                inventorySlot.setAltItems(altList.stream().map(this::getItem).toList()).withOffset(sop.getValue());

                GUIInventorySlot.Action action = this.getSlotActionByKey(key);
                if (action != null) inventorySlot.setAction(action);

                GUIInventorySlot.Check check = this.getSlotCheckByKey(key);
                if (check != null) inventorySlot.setAltCheck(check);

                SLOT_MAP.put(sop.getKey(), inventorySlot);
            }
        }
    }
    
    protected abstract GUIInventorySlot.Action getSlotActionByKey(String key);
    protected GUIInventorySlot.Check getSlotCheckByKey(String key) {
        return null;
    }

    protected void load() {
        try {
            ReflectedField<Configuration> field = new ReflectedField<>(Configuration.class, this.getClass(), "config");
            if (!field.isPublic()) field.setAccessible();
            
            Configuration config = field.getValue(this);
            loadItems(config.getConfigurationSection("Items"));
            loadSlots(config.getConfigurationSection("Slots"));
        } catch (Throwable ignored) {}
        loaded = true;
    }

    protected Map<Player, GUIPlayerInventory> inventories = new HashMap<>();

    protected GUIInventory(int size, String title) {
        this.size = size;
        this.title = title;
    }

    public Map<Integer, GUIInventorySlot> getSlotMapFor(Player player, Object... extras) {
        return new HashMap<>(SLOT_MAP);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public GUIPlayerInventory getGUIPlayerInventory(Player player, Object... extras) {
        return new GUIPlayerInventory(this, player, extras);
    }
}
