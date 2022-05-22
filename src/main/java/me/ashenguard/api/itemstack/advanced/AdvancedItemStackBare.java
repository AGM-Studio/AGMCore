package me.ashenguard.api.itemstack.advanced;

import com.cryptomorin.xseries.XMaterial;
import me.ashenguard.api.utils.SafeCallable;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class AdvancedItemStackBare extends AdvancedItemStack {
    private final Material material;
    private final short data;

    public AdvancedItemStackBare(String materialID, short data, String name, List<String> lore, boolean glow, SafeCallable<Integer> amount, int cmd) {
        super(name, lore, glow, amount, cmd);

        this.material = XMaterial.matchXMaterial(materialID).orElse(XMaterial.STONE).parseMaterial();
        this.data = data;
    }
    public AdvancedItemStackBare(String materialID, short data) {
        this(materialID, data, null, null, false, ONE, -1);
    }
    public AdvancedItemStackBare(Material material, String name, List<String> lore, boolean glow, SafeCallable<Integer> amount, int cmd) {
        super(name, lore, glow, amount, cmd);

        this.material = material;
        this.data = 0;
    }
    public AdvancedItemStackBare(Material material) {
        this(material, null, null, false, ONE, -1);
    }

    public static AdvancedItemStackBare fromSection(ConfigurationSection section) {
        return new AdvancedItemStackBare(
                section.getString("Material.ID", "STONE"),
                (short) section.getInt("Material.Data", section.getInt("Material.Value", 0)),
                section.getString("Name", null),
                section.isList("Lore") ? section.getStringList("Lore") : null,
                section.getBoolean("Glow", false),
                AdvancedItemStack.amountFromString(section.getString("Amount")),
                section.getInt("CustomModelData", section.getInt("CMD", -1))
        );
    }

    @SuppressWarnings("deprecation")
    @Override public @NotNull ItemStack getBasicItem() {
        return XMaterial.isNewVersion() ? new ItemStack(material) : new ItemStack(material, 1, (short) 0);
    }
}
