package me.ashenguard.api.gui;

import com.cryptomorin.xseries.XMaterial;
import me.ashenguard.api.Configuration;
import me.ashenguard.api.messenger.Messenger;
import me.ashenguard.api.spigot.SpigotPlugin;
import me.ashenguard.api.versions.MCVersion;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@SuppressWarnings({"UnusedReturnValue", "unused", "ConstantConditions"})
public class GUI implements Listener {
    public static final ItemStack NULL = new ItemStack(Material.STONE);
    public static final String PLAYER_HEAD = "Player_Head";
    public static final String CUSTOM_HEAD = "Custom_Head";

    public final Configuration config;
    public final Messenger messenger;
    public final SpigotPlugin plugin;

    private void translateLegacy(ConfigurationSection section) {
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            if (key.equals("Material") && section.contains("Material.ID") && !(section.contains("Material.Value") || section.contains("Material.Data"))) {
                String materialName = section.getString("Material.ID");
                if (materialName.equals(CUSTOM_HEAD) || materialName.equals(PLAYER_HEAD)) continue;
                XMaterial xMaterial = XMaterial.matchXMaterial(materialName).orElse(XMaterial.STONE);
                if (MCVersion.isLegacy()) {
                    Material material = xMaterial.parseMaterial();
                    if (material == null) material = Material.STONE;
                    section.set("Material.ID", material.name());
                    section.set("Material.Data", xMaterial.getData());
                }
            } else if (section.isConfigurationSection(key)) {
                translateLegacy(section.getConfigurationSection(key));
            }
        }

        config.saveConfig();
    }

    public GUI(SpigotPlugin plugin) {
        this(plugin, "GUI.yml");
    }

    public GUI(SpigotPlugin plugin, String config) {
        this(plugin, new Configuration(plugin, config, true));
    }

    public GUI(SpigotPlugin plugin, Configuration config) {
        this.plugin = plugin;
        this.config = config;
        this.messenger = plugin.messenger;

        if (MCVersion.isLegacy()) translateLegacy(config);
        registerListeners();

        messenger.Debug("GUI", "§5GUI§r has been loaded");
    }

    public void registerListeners() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        messenger.Debug("GUI", "§5GUI§r Listeners has been registered");
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

        messenger.Debug("GUI", "Inventory click detected", "Player= §6" + player.getName(), "Inventory= §6" + guiInventory.title);
        guiInventory.click(event);
    }

    @EventHandler
    public void onClose(@NotNull InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        GUIInventory guiInventory = removeGUIInventory(player);
        if (guiInventory != null) messenger.Debug("GUI", "Inventory close detected", "Player= §6" + player.getName(), "Inventory= §6" + guiInventory.title);
    }

    public ItemStack getItemStack(OfflinePlayer player, String path) {
        return Optional.of(getItemStack(player, config.getConfigurationSection(path))).orElse(NULL.clone());
    }
    public static ItemStack getItemStack(OfflinePlayer player, @NotNull ConfigurationSection configurationSection) {
        return ItemMaker.fromConfig(player, configurationSection).createItem();
    }

    public static ItemStack getItemStack(@NotNull ItemStack itemStack, OfflinePlayer player, String name, List<String> lore) {
        return getItemStack(itemStack, player, name, lore, false);
    }
    public static ItemStack getItemStack(@NotNull ItemStack itemStack, OfflinePlayer player, String name, List<String> lore, boolean glow) {
        return ItemMaker.designItem(itemStack, player, name, lore, glow, 1);
    }
}

