package me.ashenguard.lib.events.door;

import me.ashenguard.agmcore.AGMCore;
import me.ashenguard.agmcore.AGMEvents;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class DoorManager implements Listener {
    public static void activate() {
        AGMEvents.activateDoorEvent();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void PlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        String type = block == null ? "null" : block.getType().name().toLowerCase();
        // Ignoring any non-door and iron doors
        if (!type.contains("door") || type.contains("iron")) return;

        // Sneak-Click check
        if (player.isSneaking() && player.getEquipment() != null) {
            ItemStack item = player.getEquipment().getItemInMainHand();
            if (!item.getType().name().toLowerCase().contains("air")) return;
        }

        Openable openable = (Openable) block.getBlockData();
        DoorAction action = openable.isOpen() ? DoorAction.CLOSE : DoorAction.OPEN;

        PlayerInteractDoorEvent callEvent = new PlayerInteractDoorEvent(player, block, action);
        Bukkit.getPluginManager().callEvent(callEvent);
        if (callEvent.isCancelled()) event.setCancelled(true);
    }

    @EventHandler
    public void BlockUpdate(BlockRedstoneEvent event) {
        if (event.getOldCurrent() > 0 && event.getNewCurrent() > 0) return;
        if (event.getOldCurrent() == 0 && event.getNewCurrent() == 0) return;

        Block block = event.getBlock();
        String type = block.getType().name().toLowerCase();
        AGMCore.getMessenger().Warning(block.getType().name());
        if (!type.contains("door")) return;

        DoorAction action = event.getNewCurrent() > 0 ? DoorAction.OPEN : DoorAction.CLOSE;
        RedstoneDoorEvent callEvent = new RedstoneDoorEvent(block, action);
        Bukkit.getPluginManager().callEvent(callEvent);
    }

    public enum DoorAction {
        OPEN, CLOSE
    }

    public enum DoorType {
        DOOR, TRAP_DOOR
    }
}
