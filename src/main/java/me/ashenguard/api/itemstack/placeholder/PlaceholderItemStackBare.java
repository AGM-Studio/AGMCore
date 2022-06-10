package me.ashenguard.api.itemstack.placeholder;

import com.cryptomorin.xseries.XMaterial;
import me.ashenguard.api.Configuration;
import me.ashenguard.api.utils.Pair;
import me.ashenguard.api.utils.SafeCallable;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class PlaceholderItemStackBare extends PlaceholderItemStack {
    private final Material material;
    private final short data;

    public PlaceholderItemStackBare(String materialID, short data, String name, List<String> lore, boolean glow, SafeCallable<Integer> amount, int cmd, Map<NamespacedKey, Integer> enchants) {
        super(name, lore, glow, amount, cmd, enchants);

        this.material = XMaterial.matchXMaterial(materialID).orElse(XMaterial.STONE).parseMaterial();
        this.data = data;
    }
    public PlaceholderItemStackBare(String materialID, short data) {
        this(materialID, data, null, null, false, ONE, -1, new HashMap<>());
    }
    public PlaceholderItemStackBare(Material material, String name, List<String> lore, boolean glow, SafeCallable<Integer> amount, int cmd, Map<NamespacedKey, Integer> enchants) {
        super(name, lore, glow, amount, cmd, enchants);

        this.material = material;
        this.data = 0;
    }
    public PlaceholderItemStackBare(Material material) {
        this(material, null, null, false, ONE, -1, new HashMap<>());
    }

    public static PlaceholderItemStackBare fromSection(ConfigurationSection section) {
        Map<NamespacedKey, Integer> enchants = new HashMap<>();
        Pattern pattern = Pattern.compile("^(.+):(\\d+)$");
        section.getStringList("Enchantments").stream().filter((string) -> string != null && !string.isEmpty())
                .map((string) -> {
                    Matcher matcher = pattern.matcher(string);
                    if (matcher.find())
                        return new Pair<>(NamespacedKey.fromString(matcher.group(1)), Integer.parseInt(matcher.group(2)));
                    return new Pair<>(NamespacedKey.fromString(string), -1);
                }).filter((pair) -> pair.getKey() != null)
                .forEach((pair) -> enchants.put(pair.getKey(), pair.getValue()));

        return new PlaceholderItemStackBare(
                Configuration.getString(section, Arrays.asList("Material", "Material.ID"), "STONE"),
                (short) Configuration.getInt(section, Arrays.asList("Data", "Material.Data", "Material.Value"), 0),

                section.getString("Name", null),
                section.isList("Lore") ? section.getStringList("Lore") : null,
                section.getBoolean("Glow", false),
                PlaceholderItemStack.amountFromString(section.getString("Amount")),
                section.getInt("CustomModelData", section.getInt("CMD", -1)),
                enchants
        );
    }

    @SuppressWarnings("deprecation")
    @Override public @NotNull ItemStack getBasicItem() {
        return XMaterial.isNewVersion() ? new ItemStack(material) : new ItemStack(material, 1, (short) 0);
    }
}
