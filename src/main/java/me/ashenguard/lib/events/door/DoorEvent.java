package me.ashenguard.lib.events.door;

import org.bukkit.block.Block;

public interface DoorEvent {
    Block getBlock();
    DoorManager.DoorAction getAction();
    DoorManager.DoorType getDoorType();
}
