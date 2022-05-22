package me.ashenguard.api.itemstack.advanced;

import com.cryptomorin.xseries.XMaterial;
import me.ashenguard.api.utils.SafeCallable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class AdvancedItemStackPH extends AdvancedItemStack {
    private final String username;

    public AdvancedItemStackPH(String username, String name, List<String> lore, boolean glow, SafeCallable<Integer> amount) {
        super(name, lore, glow, amount);

        this.username = username;
    }

    public static AdvancedItemStackPH fromSection(ConfigurationSection section) {
        return new AdvancedItemStackPH(
                section.getString("Material.Player", section.getString("Material.Value", "self")),
                section.getString("Name", null),
                section.isList("Lore") ? section.getStringList("Lore") : null,
                section.getBoolean("Glow", false),
                AdvancedItemStack.amountFromString(section.getString("Amount"))
        );
    }

    @SuppressWarnings("deprecation")
    @Override public ItemStack getItem(OfflinePlayer player) {
        ItemStack item = super.getItem(player);
        OfflinePlayer target = username.equals("self") && player != null ? player : Bukkit.getOfflinePlayer(username);
        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setOwningPlayer(target);
            item.setItemMeta(skullMeta);
        }
        return item;
    }

    @Override
    public @NotNull ItemStack getBasicItem() {
        return Optional.ofNullable(XMaterial.PLAYER_HEAD.parseItem()).orElse(new ItemStack(Material.PLAYER_HEAD));
    }
}
