package me.ashenguard.api.spigot;

import me.ashenguard.api.utils.WebReader;
import org.json.JSONObject;

public class SpigotAuthor {
    public final int ID;
    public final String avatar;
    public final String name;

    public SpigotAuthor(int ID) {
        this.ID = ID;

        JSONObject jsonObject = new WebReader(String.format("https://api.spiget.org/v2/authors/%d/", ID)).readJSON();
        if (jsonObject == null || ID < 0) {
            this.name = "404 NotFound";
            this.avatar = "";
        } else {
            this.name = jsonObject.optString("name");
            this.avatar = String.format("https://www.spigotmc.org/%s", jsonObject.getJSONObject("icon").getString("url"));
        }
    }
}
