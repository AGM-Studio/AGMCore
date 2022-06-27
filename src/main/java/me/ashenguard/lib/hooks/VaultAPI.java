package me.ashenguard.lib.hooks;

import me.ashenguard.agmcore.AGMCore;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Arrays;

import static org.bukkit.Bukkit.getServer;

@SuppressWarnings({"unused"})
public class VaultAPI {
    private static Permission permission = null;
    private static Economy economy = null;
    private static Chat chat = null;

    public static boolean isEconomyEnabled() {
        return economy != null;
    }
    public static boolean isChatEnabled() {
        return chat != null;
    }
    public static boolean isPermissionsEnabled() {
        return permission != null;
    }

    public static void setup() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            AGMCore.getMessenger().debug("Hook", "Unable to access §6Vault§r, Plugin not found!");
            return;
        }

        RegisteredServiceProvider<Permission> P_RSP = getServer().getServicesManager().getRegistration(Permission.class);
        if (P_RSP != null) permission = P_RSP.getProvider();

        RegisteredServiceProvider<Economy> E_RSP = getServer().getServicesManager().getRegistration(Economy.class);
        if (E_RSP != null) economy = E_RSP.getProvider();

        RegisteredServiceProvider<Chat> C_RSP = getServer().getServicesManager().getRegistration(Chat.class);
        if (C_RSP != null) chat = C_RSP.getProvider();

        AGMCore.getMessenger().debug("Hook", "Vault hook was successful. status: ",
                "Vault Permission: §6" + (isPermissionsEnabled()? "§aEnable": "§cDisable"),
                "Vault Economy: §6" + (isEconomyEnabled()? "§aEnable": "§cDisable"),
                "Vault Chat: §6" + (isChatEnabled()? "§aEnable": "§cDisable"));
    }

    public double getPlayerBalance(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    public EconomyResponse withdrawPlayerMoney(OfflinePlayer player, double amount) {
        EconomyResponse response = economy.withdrawPlayer(player, amount);
        if (response.transactionSuccess())
            AGMCore.getMessenger().debug("Vault", String.format("§6%s§r's %.1f transaction (Withdraw) has been§a successful§r", player.getName(), amount));
        else
            AGMCore.getMessenger().debug("Vault",String.format("§6%s§r's %.1f transaction (Withdraw) has been§c failed§r", player.getName(), amount));
        return response;
    }
    public EconomyResponse depositPlayerMoney(OfflinePlayer player, double amount) {
        EconomyResponse response = economy.depositPlayer(player, amount);
        if (response.transactionSuccess())
            AGMCore.getMessenger().debug("Vault", String.format("§6%s§r's %.1f transaction (Deposit) has been§a successful§r", player.getName(), amount));
        else
            AGMCore.getMessenger().debug("Vault",String.format("§6%s§r's %.1f transaction (Deposit) has been§c failed§r", player.getName(), amount));
        return response;
    }

    public boolean playerGroupExists(String group) {
        return Arrays.stream(permission.getGroups()).toList().contains(group.toLowerCase());
    }
}
