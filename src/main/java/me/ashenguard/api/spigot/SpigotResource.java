package me.ashenguard.api.spigot;

import me.ashenguard.api.utils.WebReader;
import me.ashenguard.api.versions.Version;
import org.json.JSONObject;

import java.time.Instant;
import java.util.Date;

public class SpigotResource {
    public final int ID;
    public final Version version;
    public final String logo;

    public final String name;
    public final String tag;
    public final String alternativeSupport;
    public final String additionalInformation;
    public final String page;
    public final String downloadLink;

    public final me.ashenguard.api.spigot.SpigotAuthor author;
    public final Date releaseDate;
    public final Date updateDate;
    public final int downloads;
    public final boolean premium;
    public final double price;
    public final String currency;

    public SpigotResource(int ID) {
        this.ID = ID;
        this.page = String.format("https://www.spigotmc.org/resources/%d/", ID);
        this.version = getVersion(ID);

        JSONObject jsonObject = new WebReader(String.format("https://api.spiget.org/v2/resources/%d", ID)).readJSON();
        if (jsonObject == null) {
            this.name = "404 NotFound";
            this.logo = null;
            this.tag = "The connection has been closed while trying to capture info";
            this.downloadLink = "";
            this.alternativeSupport = null;
            this.additionalInformation = null;
            this.author = new me.ashenguard.api.spigot.SpigotAuthor(-1);
            this.releaseDate = new Date();
            this.updateDate = new Date();
            this.downloads = 0;
            this.premium = false;
            this.price = 0;
            this.currency = "";
        } else {
            this.name = jsonObject.optString("name");
            this.logo = String.format("https://www.spigotmc.org/%s", jsonObject.getJSONObject("icon").getString("url"));
            this.tag = jsonObject.optString("tag");
            this.downloadLink = String.format("https://www.spigotmc.org/%s", jsonObject.getJSONObject("file").optString("url", null));

            JSONObject links = jsonObject.optJSONObject("links");
            if (links == null) {
                this.alternativeSupport = null;
                this.additionalInformation = null;
            } else {
                this.alternativeSupport = links.optString("alternativeSupport", null);
                this.additionalInformation = links.optString("additionalInformation", null);
            }

            this.author = new SpigotAuthor(jsonObject.getJSONObject("author").getInt("id"));
            this.releaseDate = Date.from(Instant.ofEpochSecond(jsonObject.optInt("releaseDate")));
            this.updateDate = Date.from(Instant.ofEpochSecond(jsonObject.optInt("updateDate")));
            this.downloads = jsonObject.optInt("downloads");
            this.premium = jsonObject.optBoolean("premium");
            this.price = jsonObject.optDouble("price");
            this.currency = jsonObject.optString("currency");
        }
    }

    public static Version getVersion(int ID) {
        try {
            return new Version(new WebReader("https://api.spigotmc.org/legacy/update.php?resource=" + ID).read());
        } catch (Exception ignored) {
            return new Version(1, 0);
        }
    }
}
