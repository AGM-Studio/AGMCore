package me.ashenguard.api.gui;

import com.cryptomorin.xseries.XMaterial;
import me.ashenguard.agmcore.AGMCore;
import me.ashenguard.api.AdvancedListener;
import me.ashenguard.api.Configuration;
import me.ashenguard.api.versions.MCVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"UnusedReturnValue", "unused", "ConstantConditions"})
public class GUIManager extends AdvancedListener {
    public static final String PLAYER_HEAD = "Player_Head";
    public static final String CUSTOM_HEAD = "Custom_Head";
    public static final int RATE = AGMCore.getInstance().getConfig().getInt("GUIUpdate", 4);

    public static GUIManager instance = null;
    public static GUIManager getInstance() {
        if (instance == null) instance = new GUIManager();
        return instance;
    }
    public static void setInstance(GUIManager instance) {
        if (GUIManager.instance == null) GUIManager.instance = instance;
    }

    public static int getTick() {
        return instance.getGUITick();
    }

    public static void updateAll() {
        instance.updateAllInventories();
    }
    public static void update(GUIPlayerInventory inventory) {
        instance.updateInventory(inventory);
    }

    public static void closeAll() {
        instance.closeAllInventories();
    }
    public static void close(Player player) {
        player.closeInventory();
        instance.closeInventory(player);
    }

    public static GUIPlayerInventory open(GUIInventory gui, Player player, Object... extras) {
        return instance.openInventory(gui, player, extras);
    }

    private final AtomicInteger tick = new AtomicInteger(0);
    private final Map<Player, GUIPlayerInventory> inventoryMap = new HashMap<>();
    
    public GUIManager() {
        if (RATE > 0) Bukkit.getScheduler().runTaskTimer(AGMCore.getInstance(), () -> {
            tick.getAndIncrement();
            updateAllInventories();
        }, RATE, RATE);
    }

    protected void updateAllInventories() {
        inventoryMap.values().forEach(this::updateInventory);
    }
    protected void updateInventoryTitle(GUIPlayerInventory inventory) {
        // Only possible with NMS
    }
    protected void updateInventorySlots(GUIPlayerInventory inventory) {
        inventory.design();
        inventory.getPlayer().updateInventory();
    }
    protected void updateInventory(GUIPlayerInventory inventory) {
        updateInventorySlots(inventory);
        updateInventoryTitle(inventory);
    }

    protected void closeAllInventories() {
        inventoryMap.keySet().forEach(this::closeInventory);
    }
    protected void closeInventory(Player player) {
        inventoryMap.remove(player);
        player.closeInventory();
    }

    protected GUIPlayerInventory openInventory(GUIInventory gui, Player player, Object... extras) {
        GUIPlayerInventory previous = inventoryMap.remove(player);
        if (previous != null) closeInventory(player);

        GUIPlayerInventory inventory = gui.getGUIPlayerInventory(player, extras);
        inventoryMap.put(player, inventory);
        inventory.open();

        return inventory;
    }

    public int getGUITick() {
        return tick.get();
    }

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

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        GUIPlayerInventory playerInventory = inventoryMap.get(player);
        if (playerInventory == null || (inventory == null || inventory.getType() == InventoryType.PLAYER)) return;

        AGMCore.getMessenger().debug("GUI", "Inventory click detected", String.format("Player= §6%s", player.getName()), String.format("Slot= §6%d", event.getSlot()));

        if (playerInventory.trigger(event)) event.setCancelled(true);
    }

    @EventHandler
    public void onClose(@NotNull InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        GUIPlayerInventory inventory = inventoryMap.remove(player);
        if (inventory != null) close(player);
    }
}

