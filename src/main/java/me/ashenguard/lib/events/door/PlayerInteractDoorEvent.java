package me.ashenguard.lib.events.door;

import me.ashenguard.exceptions.BlockTypeException;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerInteractDoorEvent extends PlayerEvent implements Cancellable, DoorEvent {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private final Block door;
    private final DoorManager.DoorAction action;
    private final DoorManager.DoorType type;

    public PlayerInteractDoorEvent(@NotNull Player player, @NotNull Block door, DoorManager.DoorAction action) {
        super(player);
        this.door = door;
        this.action = action;

        String type = door.getType().name().toLowerCase();
        if (!type.contains("door")) throw new BlockTypeException("Expected a 'DOOR' or 'TRAP_DOOR'");
        this.type = type.contains("trap") ? DoorManager.DoorType.TRAP_DOOR : DoorManager.DoorType.DOOR;
    }

    @Override public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public Block getBlock() {
        return door;
    }

    @Override
    public DoorManager.DoorAction getAction() {
        return action;
    }

    @Override
    public DoorManager.DoorType getDoorType() {
        return type;
    }
}
