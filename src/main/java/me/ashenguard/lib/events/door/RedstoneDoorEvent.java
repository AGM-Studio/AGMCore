package me.ashenguard.lib.events.door;

import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RedstoneDoorEvent extends Event implements DoorEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Block door;
    private final DoorManager.DoorAction action;
    private final DoorManager.DoorType type;


    public RedstoneDoorEvent(Block door, DoorManager.DoorAction action) {
        this.door = door;
        this.action = action;

        String type = door.getType().name().toLowerCase();
        if (!(type.contains("door") || type.contains("gate"))) throw new IllegalArgumentException("Expected a 'DOOR', 'GATE' or 'TRAP_DOOR'");
        this.type = type.contains("gate") ? DoorManager.DoorType.GATE : type.contains("trap") ? DoorManager.DoorType.TRAP_DOOR : DoorManager.DoorType.DOOR;
    }

    @Override public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
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
