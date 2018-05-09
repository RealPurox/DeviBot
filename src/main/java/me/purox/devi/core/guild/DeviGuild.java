package me.purox.devi.core.guild;

import me.purox.devi.core.Devi;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeviGuild {

    private Devi devi;
    private GuildSettings settings;
    private String id;

    private Document muted;
    private Document banned;
    private Document embeds;
    private List<String> autoModIgnoredRoles;
    private boolean ready = false;

    public DeviGuild(String id, Devi devi){
        this.devi = devi;
        this.id = id;
        loadSettings();
    }

    private Document createFreshGuildData() {
        Document document = new Document();
        for (GuildSettings.Settings setting : GuildSettings.Settings.values()) {
            document.append(setting.name().toLowerCase(), setting.getDefaultValue());
        }
        document.append("embeds", new HashMap<>());
        document.append("muted", new HashMap<>());
        document.append("banned", new HashMap<>());
        document.append("auto_mod_ignored_roles", new ArrayList<>());
        devi.getDatabaseManager().saveToDatabase("guilds", document, id);
        return document;
    }

    public void saveSettings() {
        Document document = new Document();
        for (GuildSettings.Settings settings : GuildSettings.Settings.values()) {
            document.append(settings.name().toLowerCase(), this.settings.getValue(settings));
        }
        document.append("embeds", embeds);
        document.append("muted", muted);
        document.append("banned", banned);
        document.append("auto_mod_ignored_roles", autoModIgnoredRoles);
        devi.getDatabaseManager().saveToDatabase("guilds", document, id);
    }

    private void loadSettings() {
        Document document = devi.getDatabaseManager().getDocument(id, "guilds");

        if (document.isEmpty()) {
            document = createFreshGuildData();
        }

        GuildSettings guildSettings = new GuildSettings(this);
        for (GuildSettings.Settings setting : GuildSettings.Settings.values()) {
            if (setting.isStringValue()) {
                guildSettings.setStringValue(setting, document.getString(setting.name().toLowerCase()) == null ? (String) setting.getDefaultValue() : document.getString(setting.name().toLowerCase()));
            } else if (setting.isBooleanValue()) {
                guildSettings.setBooleanValue(setting, document.getBoolean(setting.name().toLowerCase()) == null ? (boolean) setting.getDefaultValue() : document.getBoolean(setting.name().toLowerCase()));
            } else if (setting.isIntegerValue()) {
                guildSettings.setIntegerValue(setting, document.getInteger(setting.name().toLowerCase()) == null ? (int) setting.getDefaultValue() : document.getInteger(setting.name().toLowerCase()));
            }
        }

        Object embedsObject = document.get("embeds");
        if (embedsObject == null) {
            embeds = new Document();
        } else {
            if (embedsObject instanceof Document)
                embeds = (Document) embedsObject;
            else embeds = new Document();
        }

        Object mutedObject = document.get("muted");
        if(mutedObject == null) {
            muted = new Document();
        } else {
            if (mutedObject instanceof Document)
                muted = (Document) mutedObject;
            else muted = new Document();
        }

        Object bannedObject = document.get("banned");
        if(bannedObject == null) {
            banned = new Document();
        } else {
            if (bannedObject instanceof Document)
                banned = (Document) bannedObject;
            else banned = new Document();
        }

        Object autoModIgnoredRolesObject = document.get("auto_mod_ignored_roles");
        if (autoModIgnoredRolesObject == null) {
            autoModIgnoredRoles = new ArrayList<>();
        } else {
            if (autoModIgnoredRolesObject instanceof ArrayList) {
                autoModIgnoredRoles = (List<String>) autoModIgnoredRolesObject;
            }
        }
        this.settings = guildSettings;
        this.ready = true;
    }

    public void log(MessageEmbed embed) {
        TextChannel channel = devi.getShardManager().getTextChannelById(settings.getStringValue(GuildSettings.Settings.MOD_LOG_CHANNEL));
        if (channel != null) {
            MessageUtils.sendMessage(channel, embed);
        }
    }

    public GuildEmbed createEmbed(Document document) {
        GuildEmbed guildEmbed = new GuildEmbed();
        guildEmbed.setAuthorName((String) document.get("authorName"));
        guildEmbed.setAuthorURL((String) document.get("authorURL"));
        guildEmbed.setAuthorIconURL((String) document.get("authorIconURL"));
        guildEmbed.setColorRed((Integer) document.get("colorRed"));
        guildEmbed.setColorGreen((Integer) document.get("colorGreen"));
        guildEmbed.setColorBlue((Integer) document.get("colorBlue"));
        guildEmbed.setFooterText((String) document.get("footerText"));
        guildEmbed.setFooterIconURL((String) document.get("footerIconURL"));
        guildEmbed.setDescription((String) document.get("description"));
        guildEmbed.setImageURL((String) document.get("imageURL"));
        guildEmbed.setThumbnailURL((String) document.get("thumbnailURL"));
        guildEmbed.setTitle((String) document.get("title"));
        guildEmbed.setTitleURL((String) document.get("titleURL"));

        List<MessageEmbed.Field> fields = new ArrayList<>();
        for (Document doc : (List<Document>) document.get("fields")) {
            fields.add( new MessageEmbed.Field(doc.getString("fieldName"), doc.getString("fieldValue"), doc.getBoolean("fieldInline")));
        }
        guildEmbed.setFields(fields);

        return guildEmbed;
    }

    public GuildSettings getSettings() {
        return settings;
    }

    public Document getMuted() {
        return muted;
    }

    public Document getBanned() {
        return banned;
    }

    boolean isReady() {
        return ready;
    }

    public String getId() {
        return id;
    }

    public Document getEmbeds() {
        return embeds;
    }

    public List<String> getAutoModIgnoredRoles() {
        return autoModIgnoredRoles;
    }

    public Devi getDevi() {
        return devi;
    }
}
