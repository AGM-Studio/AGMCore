package me.ashenguard.api.gui;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import me.ashenguard.api.Configuration;
import me.ashenguard.api.messenger.Messenger;
import me.ashenguard.api.placeholderapi.PAPI;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class GUI implements Listener {
    private final JavaPlugin plugin;
    private final PAPI PAPI;
    private final boolean legacy;

    public Configuration config;

    private void translateLegacy(@NotNull ConfigurationSection section) {
        for (String key: section.getKeys(false)) {
            if (key.equals("Material") && section.contains("Material.ID")) {
                String materialName = section.getString("Material.ID");
                if (materialName.equals("CUSTOM_HEAD") || materialName.equals("PLAYER_HEAD")) continue;
                XMaterial xMaterial = XMaterial.matchXMaterial(materialName).orElse(XMaterial.STONE);
                if (legacy) {
                    Material material = xMaterial.parseMaterial(true);
                    if (material == null) material = Material.STONE;
                    section.set("Material.ID", material.name());
                    section.set("Material.Value", xMaterial.getData());
                }
            } else if (section.isConfigurationSection(key)) {
                translateLegacy(section.getConfigurationSection(key));
            }
        }

        config.saveConfig();
    }

    public GUI(JavaPlugin plugin, PAPI PAPI, boolean legacy) {
        this.plugin = plugin;
        this.PAPI = PAPI;
        this.legacy = legacy;

        config = new Configuration(plugin, "GUI.yml", true);
        if (legacy) translateLegacy(config);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        Messenger.getInstance(plugin).Debug("GUI", "§5GUI§r has been loaded and its Listener has been registered");
    }

    // ---- GUI Inventories ---- //
    private HashMap<Player, GUIInventory> inventoryHashMap = new HashMap<>();

    public void saveGUIInventory(Player player, GUIInventory inventory) {
        inventoryHashMap.put(player, inventory);
    }
    public void removeGUIInventory(Player player) {
        inventoryHashMap.remove(player);
    }
    public void closeAll() {
        for (GUIInventory guiInventory : inventoryHashMap.values()) {
            guiInventory.close();
        }
    }

    /**
     * This event handler detect clicks on a GUIInventory and pass event to Click method in {@link GUIInventory}
     *
     * @param event Inventory Click Event
     */
    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        GUIInventory guiInventory = inventoryHashMap.getOrDefault(player, null);
        if (guiInventory == null || event.getClickedInventory().getType() == InventoryType.PLAYER) return;

        event.setCancelled(true);

        guiInventory.click(event);
    }

    /**
     * This event handler will detect close event and remove it from hash map
     *
     * @param event Inventory Close Event
     */
    @EventHandler
    public void onClose(@NotNull InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        GUIInventory guiInventory = inventoryHashMap.getOrDefault(player, null);
        if (guiInventory == null) return;

        removeGUIInventory(player);
        Messenger.getInstance(plugin).Debug("GUI", "Inventory close detected", "Player= §6" + player.getName(), "Inventory= §6" + guiInventory.title);
    }

    // <editor-fold ---- Item Creators ---- //>
    /**
     * This Method generate item using a configuration section
     *
     * @param player target player that item will be generated for
     * @param path   grab section from this path in config
     */
    public ItemStack getItemStack(OfflinePlayer player, String path) {
        ConfigurationSection section = config.getConfigurationSection(path);
        return getItemStack(player, section);
    }

    /**
     * This Method generate item using a configuration section
     *
     * @param player               target player that item will be generated for
     * @param configurationSection AGM GUI Item Tree section
     */
    public ItemStack getItemStack(OfflinePlayer player, @NotNull ConfigurationSection configurationSection) {
        String ID = configurationSection.getString("Material.ID", "STONE");
        String value = configurationSection.getString("Material.Value", "");
        int intValue = configurationSection.getInt("Material.Value", 0);
        String name = configurationSection.getString("Name", "§r");
        List<String> oldLore = configurationSection.getStringList("Lore");
        boolean glow = configurationSection.getBoolean("Glow", false);

        return getItemStack(player, ID, value, (short) intValue, name, oldLore, glow);
    }

    /**
     * This Method generate item using other provided methods
     *
     * @param player   target player that item will be generated for
     * @param name     item name; It will be translated for target player
     * @param lore     item lore; It will be translated for target player
     * @param ID       the item material ID; It can be Custom_Head, Player_Head or a vanilla material
     * @param value    the item value; It would be used in creating Player_Head as Player's name or self to use target player or skin value for Custom_Head
     * @param intValue the data value; It would be used in vanilla material and legacy version
     */
    public ItemStack getItemStack(OfflinePlayer player, String ID, String value, short intValue, String name, List<String> lore) {
        return getItemStack(player, ID, value, intValue, name, lore, false);
    }
    public ItemStack getItemStack(OfflinePlayer player, String ID, String value, short intValue, String name, List<String> lore, boolean glow) {
        ItemStack item = getItemStack(player, ID, value, intValue);
        return getItemStack(item, player, name, lore, glow);
    }

    /**
     * This Method give name and lore (and glow) to item given
     *
     * @param player    target player that item will be generated for
     * @param name      item name; It will be translated for target player
     * @param lore      item lore; It will be translated for target player
     * @param itemStack the item; name and lore will be set for this item
     */
    public ItemStack getItemStack(@NotNull ItemStack itemStack, OfflinePlayer player, String name, List<String> lore) {
        name = PAPI.translate(player, name);
        lore = PAPI.translate(player, lore);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
    public ItemStack getItemStack(@NotNull ItemStack itemStack, OfflinePlayer player, String name, List<String> lore, boolean glow) {
        ItemMeta itemMeta = getItemStack(itemStack, player, name, lore).getItemMeta();
        if (glow) {
            itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    /**
     * This Method only generate a bare Item/Head based on ID and value
     *
     * @param player   target player that item will be generated for
     * @param ID       the item material ID; It can be Custom_Head, Player_Head or a vanilla material
     * @param value    the item value; It would be used in creating Player_Head as Player's name or self to use target player or skin value for Custom_Head
     * @param intValue the data value; It would be used in vanilla material and legacy version
     */
    public ItemStack getItemStack(OfflinePlayer player, @NotNull String ID, String value, short intValue) {
        if (ID.equals("Custom_Head") || ID.equals("Player_Head"))
            return getItemHead(player, ID.equals("Custom_Head"), value);
        return getItemStack(ID, intValue);
    }

    /**
     * This Method only generate a bare Item based on ID and value
     *
     * @param ID   the item material ID; It can be Custom_Head, Player_Head or a vanilla material
     * @param data the data value; It would be used for legacy version
     */
    public ItemStack getItemStack(String ID, short data) {
        ItemStack item = XMaterial.matchXMaterial(ID).orElse(XMaterial.STONE).parseItem();
        if (item == null) return null;

        if (legacy) item.setDurability(data);

        return item;
    }

    /**
     * This Method only generate a bare head based on value
     *
     * @param player target player that item will be generated for.
     * @param custom is head a custom head or a player head.
     * @param value  skin value for custom head or value for player head as player's name or self to use target player
     */
    public ItemStack getItemHead(OfflinePlayer player, boolean custom, String value) {
        ItemStack item = XMaterial.PLAYER_HEAD.parseItem();
        if (item == null) return null;

        SkullMeta result = SkullUtils.applySkin(item.getItemMeta(), custom || !value.equals("self") ? value : (player != null ? player.getName() : "Steve"));
        item.setItemMeta(result);

        return item;
    }
    // </editor-fold>
}

