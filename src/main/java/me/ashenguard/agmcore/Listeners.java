package me.ashenguard.agmcore;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static org.bukkit.Bukkit.getServer;

public class Listeners implements Listener {
    public Listeners() {
        getServer().getPluginManager().registerEvents(this, AGMCore.getInstance());

        AGMCore.Messenger.Debug("General", "Listeners has been registered");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        AGMCore.Messenger.updateNotification(event.getPlayer(), AGMCore.spigotupdater);
    }
}
