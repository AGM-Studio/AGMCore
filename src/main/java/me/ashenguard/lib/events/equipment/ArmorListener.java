package me.ashenguard.lib.events.equipment;

import me.ashenguard.api.spigot.SpigotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ArmorListener implements Listener {
    private static final Predicate<Action> RIGHT_CLICK = action -> action.name().startsWith("RIGHT_CLICK");
    private static final List<String> BLACK_LISTED_MATERIAL = Arrays.asList("FURNACE", "CHEST", "TRAPPED_CHEST", "BEACON", "DISPENSER", "DROPPER", "HOPPER", "WORKBENCH", "ENCHANTMENT_TABLE", "ENDER_CHEST", "ANVIL", "BED_BLOCK", "FENCE_GATE", "SPRUCE_FENCE_GATE", "BIRCH_FENCE_GATE", "ACACIA_FENCE_GATE", "JUNGLE_FENCE_GATE", "DARK_OAK_FENCE_GATE", "IRON_DOOR_BLOCK", "WOODEN_DOOR", "SPRUCE_DOOR", "BIRCH_DOOR", "JUNGLE_DOOR", "ACACIA_DOOR", "DARK_OAK_DOOR", "WOOD_BUTTON", "STONE_BUTTON", "TRAP_DOOR", "IRON_TRAPDOOR", "DIODE_BLOCK_OFF", "DIODE_BLOCK_ON", "REDSTONE_COMPARATOR_OFF", "REDSTONE_COMPARATOR_ON", "FENCE", "SPRUCE_FENCE", "BIRCH_FENCE", "JUNGLE_FENCE", "DARK_OAK_FENCE", "ACACIA_FENCE", "NETHER_FENCE", "BREWING_STAND", "CAULDRON", "LEGACY_SIGN_POST", "LEGACY_WALL_SIGN", "LEGACY_SIGN", "ACACIA_SIGN", "ACACIA_WALL_SIGN", "BIRCH_SIGN", "BIRCH_WALL_SIGN", "DARK_OAK_SIGN", "DARK_OAK_WALL_SIGN", "JUNGLE_SIGN", "JUNGLE_WALL_SIGN", "OAK_SIGN", "OAK_WALL_SIGN", "SPRUCE_SIGN", "SPRUCE_WALL_SIGN", "LEVER", "BLACK_SHULKER_BOX", "BLUE_SHULKER_BOX", "BROWN_SHULKER_BOX", "CYAN_SHULKER_BOX", "GRAY_SHULKER_BOX", "GREEN_SHULKER_BOX", "LIGHT_BLUE_SHULKER_BOX", "LIME_SHULKER_BOX", "MAGENTA_SHULKER_BOX", "ORANGE_SHULKER_BOX", "PINK_SHULKER_BOX", "PURPLE_SHULKER_BOX", "RED_SHULKER_BOX", "SILVER_SHULKER_BOX", "WHITE_SHULKER_BOX", "YELLOW_SHULKER_BOX", "DAYLIGHT_DETECTOR_INVERTED", "DAYLIGHT_DETECTOR", "BARREL", "BLAST_FURNACE", "SMOKER", "CARTOGRAPHY_TABLE", "COMPOSTER", "GRINDSTONE", "LECTERN", "LOOM", "STONECUTTER", "BELL");
    private static final List<SlotType> SLOT_TYPES = Arrays.asList(SlotType.ARMOR, SlotType.QUICKBAR, SlotType.CONTAINER);
    private static final List<InventoryType> INVENTORY_TYPES = Arrays.asList(InventoryType.CRAFTING, InventoryType.PLAYER);

    public ArmorListener(SpigotPlugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        try { Bukkit.getServer().getPluginManager().registerEvents(new DispenserListener(), plugin);
        } catch (Throwable ignored) {}
    }

    private boolean callEvent(Player player, ArmorEquipEvent.EquipMethod method, ArmorType type, ItemStack oldArmor, ItemStack newArmor) {
        ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(player, method, type, oldArmor, newArmor);
        Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
        return armorEquipEvent.isCancelled();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public final void inventoryClick(final InventoryClickEvent event) {
        if (event.getAction() == InventoryAction.NOTHING || event.getClickedInventory() == null) return;
        if (!SLOT_TYPES.contains(event.getSlotType()) || !INVENTORY_TYPES.contains(event.getInventory().getType()))
            return;
        if (!event.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        final Player player = (Player) event.getWhoClicked();

        final boolean shift = event.getClick().equals(ClickType.SHIFT_LEFT) || event.getClick().equals(ClickType.SHIFT_RIGHT);
        final boolean number = event.getClick().equals(ClickType.NUMBER_KEY);

        ArmorEquipEvent.EquipMethod method = event.getAction().equals(InventoryAction.HOTBAR_SWAP) || number ? ArmorEquipEvent.EquipMethod.HOTBAR_SWAP : ArmorEquipEvent.EquipMethod.PICK_DROP;
        ItemStack newArmor = event.getCursor();
        ItemStack oldArmor = event.getCurrentItem();
        ArmorType type = ArmorType.matchType(shift ? event.getCurrentItem() : event.getCursor());
        if (!shift && type != null && event.getRawSlot() != type.getSlot()) return;

        if (shift && type != null) {
            boolean equipping = event.getRawSlot() != type.getSlot();
            if (equipping != isAirOrNull(type.getItemStack(player))) return;
            method = ArmorEquipEvent.EquipMethod.SHIFT_CLICK;
            newArmor = equipping ? null : event.getCurrentItem();
            oldArmor = equipping ? event.getCurrentItem() : null;
        } else if (!shift) {
            if (number) {
                ItemStack barItem = event.getClickedInventory().getItem(event.getHotbarButton());
                if (!isAirOrNull(barItem)) {
                    type = ArmorType.matchType(barItem);
                    newArmor = barItem;
                    oldArmor = event.getClickedInventory().getItem(event.getSlot());
                } else {
                    type = ArmorType.matchType(!isAirOrNull(event.getCurrentItem()) ? event.getCurrentItem() : event.getCursor());
                }
            } else {
                if (isAirOrNull(event.getCursor()) && !isAirOrNull(event.getCurrentItem()))
                    type = ArmorType.matchType(event.getCurrentItem());
            }
            if (type == null || event.getRawSlot() != type.getSlot()) return;
            method = event.getAction().equals(InventoryAction.HOTBAR_SWAP) || number ? ArmorEquipEvent.EquipMethod.HOTBAR_SWAP : ArmorEquipEvent.EquipMethod.PICK_DROP;
        }
        if (type != null && callEvent(player, method, type, newArmor, oldArmor)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent event) {
        if (event.useItemInHand().equals(Result.DENY) || !RIGHT_CLICK.test(event.getAction())) return;
        Player player = event.getPlayer();

        if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && !player.isSneaking())
            if (BLACK_LISTED_MATERIAL.contains(event.getClickedBlock().getType().name())) return;

        ArmorType type = ArmorType.matchType(event.getItem());
        if (type != null && isAirOrNull(type.getItemStack(player)))
            if (callEvent(player, ArmorEquipEvent.EquipMethod.HOTBAR, type, null, event.getItem())) {
                event.setCancelled(true);
                player.updateInventory();
            }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void inventoryDrag(InventoryDragEvent event) {
        ArmorType type = ArmorType.matchType(event.getOldCursor());
        if (event.getRawSlots().isEmpty()) return;
        if (type != null && type.getSlot() == event.getRawSlots().stream().findFirst().orElse(0)) {
            if (callEvent(((Player) event.getWhoClicked()), ArmorEquipEvent.EquipMethod.DRAG, type, null, event.getOldCursor())) {
                event.setResult(Result.DENY);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void itemBreakEvent(PlayerItemBreakEvent event) {
        ArmorType type = ArmorType.matchType(event.getBrokenItem());
        if (type != null) {
            Player player = event.getPlayer();
            if (callEvent(player, ArmorEquipEvent.EquipMethod.BROKE, type, event.getBrokenItem(), null)) {
                ItemStack item = event.getBrokenItem().clone();
                ItemMeta meta = item.getItemMeta();
                if (meta == null) return;
                if (meta instanceof Damageable) ((Damageable) meta).setDamage(((Damageable) meta).getDamage() - 1);
                item.setAmount(1);
                item.setItemMeta(meta);
                type.setItemStack(player, item);
            }
        }
    }

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (event.getKeepInventory()) return;
        for (ItemStack i : player.getInventory().getArmorContents()) if (!isAirOrNull(i))
            callEvent(player, ArmorEquipEvent.EquipMethod.DEATH, ArmorType.matchType(i), i, null);
    }

    public static boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }
}