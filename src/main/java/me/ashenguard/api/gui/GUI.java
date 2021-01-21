package me.ashenguard.api.gui;

import com.cryptomorin.xseries.ReflectionUtils;
import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.ashenguard.api.Configuration;
import me.ashenguard.api.messenger.PHManager;
import me.ashenguard.api.spigot.SpigotPlugin;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings({"UnusedReturnValue", "unused", "deprecation"})
public class GUI implements Listener {
    public static final ItemStack NULL = new ItemStack(Material.STONE);
    public static final String PLAYER_HEAD = "Player_Head";
    public static final String CUSTOM_HEAD = "Custom_Head";

    public final SpigotPlugin plugin;
    public final Configuration config;

    private void translateLegacy(@NotNull ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            if (key.equals("Material") && section.contains("Material.ID")) {
                String materialName = section.getString("Material.ID");
                if (materialName.equals(CUSTOM_HEAD) || materialName.equals(PLAYER_HEAD)) continue;
                XMaterial xMaterial = XMaterial.matchXMaterial(materialName).orElse(XMaterial.STONE);
                if (plugin.isLegacy()) {
                    Material material = xMaterial.parseMaterial();
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

    public GUI(SpigotPlugin plugin) {
        this.plugin = plugin;
        this.config = new Configuration(plugin, "GUI.yml", true);

        if (plugin.isLegacy()) translateLegacy(config);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.messenger.Debug("GUI", "§5GUI§r has been loaded and its Listener has been registered");
    }

    // ---- GUI Inventories ---- //
    private final HashMap<Player, GUIInventory> inventoryHashMap = new HashMap<>();
    public GUIInventory getGUIInventory(Player player) {
        return inventoryHashMap.getOrDefault(player, null);
    }
    public GUIInventory saveGUIInventory(Player player, GUIInventory inventory) {
        return inventoryHashMap.put(player, inventory);
    }
    public GUIInventory removeGUIInventory(Player player) {
        return inventoryHashMap.remove(player);
    }
    public void closeAll() {
        for (GUIInventory guiInventory : inventoryHashMap.values()) guiInventory.close();
    }

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        GUIInventory guiInventory = getGUIInventory(player);
        if (guiInventory == null || (guiInventory.inventoryOnly && (inventory == null || inventory.getType() == InventoryType.PLAYER))) return;
        if (guiInventory.cancelAlways) event.setCancelled(true);

        plugin.messenger.Debug("GUI", "Inventory click detected", "Player= §6" + player.getName(), "Inventory= §6" + guiInventory.title);
        guiInventory.click(event);
    }

    @EventHandler
    public void onClose(@NotNull InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        GUIInventory guiInventory = removeGUIInventory(player);
        if (guiInventory != null) plugin.messenger.Debug("GUI", "Inventory close detected", "Player= §6" + player.getName(), "Inventory= §6" + guiInventory.title);
    }


    public ItemStack getItemStack(OfflinePlayer player, String path) {
        return Optional.of(getItemStack(player, config.getConfigurationSection(path))).orElse(NULL.clone());
    }
    public ItemStack getItemStack(OfflinePlayer player, @NotNull ConfigurationSection configurationSection) {
        String ID = configurationSection.getString("Material.ID", null);
        String value = configurationSection.getString("Material.Value", null);
        short data = (short) configurationSection.getInt("Material.Data", configurationSection.getInt("Material.Value", 0));
        String uuidString = configurationSection.getString("Material.UUID", "");
        UUID uuid = UUID.randomUUID();
        try { uuid = UUID.fromString(uuidString); } catch (IllegalArgumentException ignored) {}

        ItemStack item = getItemStack(player, ID, value, data, uuid);
        String name = configurationSection.getString("Name", null);
        List<String> lore = configurationSection.getStringList("Lore");
        boolean glow = configurationSection.getBoolean("Glow", false);

        return getItemStack(item, player, name, lore, glow);
    }

    public ItemStack getItemStack(@NotNull ItemStack itemStack, OfflinePlayer player, String name, List<String> lore) {
        return getItemStack(itemStack, player, name, lore, false);
    }
    public ItemStack getItemStack(@NotNull ItemStack itemStack, OfflinePlayer player, String name, List<String> lore, boolean glow) {
        name = PHManager.translate(player, name);
        lore = PHManager.translate(player, lore);

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (name != null) itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);

        if (glow) {
            itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public ItemStack getItemStack(OfflinePlayer player, String ID, String value, short intValue, UUID uuid) {
        if (ID == null) return NULL.clone();
        switch (ID) {
            case PLAYER_HEAD:
                return getPlayerHead(player, value == null ? "self" : value);
            case CUSTOM_HEAD:
                return getCustomHead(value == null ? "" : value, uuid);
            default:
                return getItemStack(ID.toUpperCase(), intValue);
        }
    }

    public ItemStack getItemStack(String ID, short data) {
        Material material = XMaterial.matchXMaterial(ID).orElse(XMaterial.STONE).parseMaterial();
        if (material == null) return NULL.clone();
        return plugin.isLegacy() ? new ItemStack(material) : new ItemStack(material, 1, data);
    }
    public ItemStack getPlayerHead(OfflinePlayer player, String value) {
        ItemStack item = XMaterial.PLAYER_HEAD.parseItem();
        if (item == null) return null;

        OfflinePlayer target = value.equals("self") && player != null ? player : Bukkit.getOfflinePlayer(value);
        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        skullMeta.setOwningPlayer(target);
        item.setItemMeta(skullMeta);

        return item;
    }
    public ItemStack getCustomHead(String value, UUID uuid) {
        ItemStack item = XMaterial.PLAYER_HEAD.parseItem();
        if (item == null) return null;
        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();

        try {
            GameProfile profile = new GameProfile(uuid == null ? UUID.randomUUID() : uuid, null);
            profile.getProperties().put("textures", new Property("textures", value));
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            Class<?> craftSkull = ReflectionUtils.getCraftClass("inventory.CraftMetaSkull");
            if (craftSkull != null) {
                Field profileField = craftSkull.getDeclaredField("profile");
                profileField.setAccessible(true);
                MethodHandle gameProfile = lookup.unreflectSetter(profileField);
                if (gameProfile != null) gameProfile.invoke(skullMeta, profile);
            }
        } catch (Throwable exception) {
            plugin.messenger.handleException(exception);
        }

        item.setItemMeta(skullMeta);
        return item;
    }
}

