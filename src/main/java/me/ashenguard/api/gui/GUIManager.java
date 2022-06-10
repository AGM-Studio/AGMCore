package me.ashenguard.api.gui;

import com.cryptomorin.xseries.XMaterial;
import me.ashenguard.agmcore.AGMCore;
import me.ashenguard.api.Configuration;
import me.ashenguard.api.versions.MCVersion;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@SuppressWarnings({"UnusedReturnValue", "unused", "ConstantConditions"})
public class GUIManager implements Listener {
    public static final String PLAYER_HEAD = "Player_Head";
    public static final String CUSTOM_HEAD = "Custom_Head";
    public static final int RATE = AGMCore.getInstance().getConfig().getInt("GUIUpdate", 4);

    private int tick = 0;

    public static void translateLegacy(Configuration config, ConfigurationSection section) {
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
                translateLegacy(config, section.getConfigurationSection(key));
            }
        }

        config.saveConfig();
    }

    public GUIManager() {
        registerListeners();

        if (RATE > 0)
            AGMCore.getInstance().getServer().getScheduler().runTaskTimer(AGMCore.getInstance(), this::updateInventories, RATE, RATE);

        AGMCore.getMessenger().Debug("GUI", "§5GUIManager§r has been loaded.");
    }

    public void registerListeners() {
        AGMCore.getInstance().getServer().getPluginManager().registerEvents(this, AGMCore.getInstance());
    }

    private void updateInventories() {
        tick += 1;

        for (GUIInventory inventory: inventoryHashMap.values()) GUIUpdater.update(inventory);
    }

    public int getTick() {
        return tick;
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
        if (guiInventory == null || (inventory == null || inventory.getType() == InventoryType.PLAYER)) return;

        AGMCore.getMessenger().Debug("GUI", "Inventory click detected", String.format("Player= §6%s", player.getName()), String.format("Slot= §6%d", event.getSlot()));

        GUIInventorySlot slot = guiInventory.getSlot(event.getSlot());
        if (slot == null || slot.runAction(event)) event.setCancelled(true);
    }

    @EventHandler
    public void onClose(@NotNull InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        GUIInventory guiInventory = removeGUIInventory(player);
        if (guiInventory != null) guiInventory.close();
    }
}

