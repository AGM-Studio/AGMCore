package me.ashenguard.api.gui;

import me.ashenguard.agmcore.AGMCore;
import me.ashenguard.api.Configuration;
import me.ashenguard.api.itemstack.placeholder.PlaceholderItemStack;
import me.ashenguard.api.messenger.PlaceholderManager;
import me.ashenguard.api.placeholder.Placeholder;
import me.ashenguard.api.utils.Pair;
import me.ashenguard.exceptions.NullValue;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public abstract class GUIInventory {
    private static final GUIManager GUI_MANAGER = AGMCore.getGUIManager();

    private static final Pattern MULTI_ITEM_PATTERN = Pattern.compile("^(\\d+)x(.*?)$");
    private static final Pattern SLOT_OFFSET_PATTERN = Pattern.compile("^(\\d+)-(\\d+)$");
    private static final Pattern SLOT_PATTERN = Pattern.compile("^(\\d+)$");

    private static Pair<Integer, Integer> getSlotAndOffset(String string) {
        Matcher SOM = SLOT_OFFSET_PATTERN.matcher(string);
        Matcher SM = SLOT_PATTERN.matcher(string);
        if (SOM.find()) return new Pair<>(Integer.parseInt(SOM.group(1)), Integer.parseInt(SOM.group(2)));
        else if (SM.find()) return new Pair<>(Integer.parseInt(SM.group(1)), 0);
        else return null;
    }

    protected static class GUIData {
        protected final String TITLE;
        protected final int SIZE;

        protected GUIData(String title, int size) {
            TITLE = title;
            SIZE = size;
        }

        protected final Map<String, PlaceholderItemStack> LOCAL_ITEMS = new HashMap<>();
        protected final Map<Integer, GUIInventorySlot> SLOT_MAP = new HashMap<>();

        public void setSlot(int index, GUIInventorySlot slot) {
            if (slot == null) SLOT_MAP.remove(index);
            else SLOT_MAP.put(index, slot);
        }
        public GUIInventorySlot getSlot(int index) {
            return SLOT_MAP.getOrDefault(index, null);
        }

        protected @NotNull Pair<PlaceholderItemStack, Integer> getItem(String key) {
            int count = 1;
            Matcher MIM = MULTI_ITEM_PATTERN.matcher(key);
            if (MIM.find()) {
                count = Integer.parseInt(MIM.group(1));
                key = MIM.group(2);
            }

            String local = key.toLowerCase().startsWith("inventory:") ? key.substring(10) : key;
            PlaceholderItemStack item = LOCAL_ITEMS.getOrDefault(local.toLowerCase(), null);
            if (item != null) return new Pair<>(item, count);

            return new Pair<>(AGMCore.getItemLibrary().getNotNullItem(key), count);
        }

        protected void loadLocalItems(Configuration config) {
            ConfigurationSection section = config.getConfigurationSection("Items");
            if (section == null) return;

            for (String key : section.getKeys(false)) {
                if (section.isConfigurationSection(key)) {
                    PlaceholderItemStack item = PlaceholderItemStack.fromSection(section.getConfigurationSection(key));
                    if (item != null) LOCAL_ITEMS.put(key.toLowerCase(), item);
                }
            }
        }

        protected void loadSlotMap(GUIInventory inventory, Configuration config) {
            ConfigurationSection section = config.getConfigurationSection("Slots");
            NullValue.check("Slot section", section);

            for (String key : section.getKeys(false)) {
                List<String> slots = section.getStringList(String.format("%s.Slots", key));
                List<String> itemList = section.getStringList(String.format("%s.Items", key));

                AGMCore.getMessenger().debug("GUI", String.format("Loading %s on %s", Arrays.toString(itemList.toArray()), Arrays.toString(slots.toArray())));

                for (String slot : slots) {
                    Pair<Integer, Integer> sop = getSlotAndOffset(slot);
                    if (sop == null) continue;

                    GUIInventorySlot inventorySlot = new GUIInventorySlot(sop.getKey());
                    inventorySlot.setItems(itemList.stream().map(this::getItem).toList()).withOffset(sop.getValue());
                    Function<InventoryClickEvent, Boolean> action = inventory.getSlotActionByKey(key);
                    if (action != null) inventorySlot.setAction(action);
                    SLOT_MAP.put(sop.getKey(), inventorySlot);
                }
            }
        }
    }

    protected final Player player;
    protected final GUIData data;

    protected final List<Placeholder> placeholders = new ArrayList<>();

    protected final Inventory inventory;

    protected GUIInventory(Player player, Configuration config) {
        this.player = player;
        this.data = new GUIData(config.getString("Title", "AGM Inventory"), config.getInt("Size", 6) * 9);

        this.inventory = Bukkit.createInventory(player, getSize(), getTitle());

        this.data.loadLocalItems(config);
        this.data.loadSlotMap(this, config);
    }

    protected abstract Function<InventoryClickEvent, Boolean> getSlotActionByKey(String key);

    public void show() {
        design();
        player.openInventory(inventory);

        GUI_MANAGER.saveGUIInventory(player, this);
        AGMCore.getMessenger().Debug("GUI", "GUI inventory opened", "Player= §6" + player.getName());
    }

    public void close() {
        GUI_MANAGER.removeGUIInventory(player);
        AGMCore.getMessenger().Debug("GUI", "GUI inventory closed", "Player= §6" + player.getName());

        // Due packets and pings, Inventory might not close with a simple close call...
        Bukkit.getScheduler().runTaskLater(AGMCore.getInstance(), () -> {
            if (GUI_MANAGER.getGUIInventory(player) == null) player.closeInventory();
        }, 1);
    }

    public void design() {
        int tick = GUI_MANAGER.getTick();
        for (GUIInventorySlot slot : data.SLOT_MAP.values()) slot.update(this, tick);
    }


    public void setSlot(int index, GUIInventorySlot slot) {
        this.data.setSlot(index, slot);
    }
    public GUIInventorySlot getSlot(int index) {
        return this.data.getSlot(index);
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getTitle() {
        return PlaceholderManager.translate(player, data.TITLE);
    }

    public int getSize() {
        return data.SIZE;
    }
}