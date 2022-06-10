package me.ashenguard.api.gui;

import com.cryptomorin.xseries.XMaterial;
import me.ashenguard.agmcore.AGMCore;
import me.ashenguard.api.messenger.PlaceholderManager;
import me.ashenguard.api.versions.MCVersion;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class GUIBook {
    private final Player player;
    private final ItemStack book = XMaterial.WRITTEN_BOOK.parseItem();

    public GUIBook(Player player, String title, String author, List<String> pages) {
        this.player = player;

        BookMeta meta = book != null ? (BookMeta) book.getItemMeta() : null;
        if (book == null || meta == null) return;

        meta.setTitle(title);
        meta.setAuthor(author);
        @NotNull List<BaseComponent[]> componentPages = new ArrayList<>();
        for (String page: PlaceholderManager.translate(player, pages)) componentPages.add(translate(page));
        meta.spigot().setPages(componentPages);

        book.setItemMeta(meta);
    }
    public GUIBook(Player player, List<String> pages) {
        this(player, "GUI BOOK", AGMCore.getInstance().getName(), pages);
    }
    public GUIBook(Player player, ConfigurationSection section) {
        this(player, section.getString("Title", "GUI BOOK"), section.getString("Author", AGMCore.getInstance().getName()), section.getStringList("Pages"));
    }

    public void open() {
        PacketManager packetManager = PacketManager.getInstance();
        if (packetManager == null) AGMCore.getMessenger().response(player, String.format("Unable to find API, Please install API%s", MCVersion.getMCVersion().name().substring(1)));
        else {
            packetManager.openBook(player, book);
        }
    }
    public void give() {
        if (book != null) player.getInventory().addItem(book);
    }
    public void close() {
        PacketManager packetManager = PacketManager.getInstance();
        if (packetManager != null) packetManager.closeBook(player);
    }

    public abstract static class PacketManager {
        private static PacketManager instance = null;

        public static PacketManager getInstance() {
            return instance;
        }

        public static void setInstance(PacketManager instance) {
            if (PacketManager.instance != null) return;
            PacketManager.instance = instance;
        }

        public abstract void openBook(Player player, ItemStack book);
        public abstract void closeBook(Player player);
    }
    public static class HyperText {
        protected static Pattern HYPER_PATTERN = Pattern.compile("\\[(.+?)]\\((.+?)\\)");

        protected static Pattern COMMAND_PATTERN = Pattern.compile("^(.*?)\s?(/.+)$");
        protected static Pattern LINK_PATTERN = Pattern.compile("^(https?://[^\\s]+)\s?(.*?)$");

        public final int start;
        public final int end;
        public final String text;
        public final String value;
        public final String hover;

        public final boolean isLink;
        public final boolean isCommand;

        public HyperText(int start, int end, String text, String value) {
            this.start = start;
            this.end = end;
            this.text = text;

            Matcher link_match = LINK_PATTERN.matcher(value);
            Matcher cmd_match = COMMAND_PATTERN.matcher(value);
            if (link_match.find()) {
                this.isLink = true;
                this.isCommand = false;
                this.hover = link_match.group(2);
                this.value = link_match.group(1);
            } else if (cmd_match.find()) {
                this.isLink = false;
                this.isCommand = true;
                this.hover = cmd_match.group(1);
                this.value = cmd_match.group(2);
            } else {
                this.isLink = false;
                this.isCommand = false;
                this.hover = value;
                this.value = "";
            }
        }

        public static List<HyperText> getHyperTexts(String text) {
            List<HyperText> list = new ArrayList<>();
            Matcher match = HYPER_PATTERN.matcher(text);
            while (match.find()) list.add(new HyperText(match.start(), match.end(), match.group(1), match.group(2)));
            return list;
        }
    }

    private BaseComponent[] translate(String page) {
        List<GUIBook.HyperText> hyperTexts = GUIBook.HyperText.getHyperTexts(page);

        ComponentBuilder text = new ComponentBuilder();
        int index = 0;
        for (GUIBook.HyperText hyper: hyperTexts) {
            if (index < hyper.start)
                text.append(new TextComponent(page.substring(index, hyper.start)), ComponentBuilder.FormatRetention.NONE);

            index = hyper.end;

            TextComponent component = new TextComponent(hyper.text);
            if (hyper.isLink) component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, hyper.value));
            if (hyper.isCommand) component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, hyper.value));
            if (hyper.hover != null && hyper.hover.length() > 0)
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hyper.hover)));
            text.append(component, ComponentBuilder.FormatRetention.NONE);
        }
        return text.create();
    }
}
