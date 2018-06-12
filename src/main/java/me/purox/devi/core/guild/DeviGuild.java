package me.purox.devi.core.guild;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import me.purox.devi.core.Devi;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class DeviGuild {

    private Devi devi;
    private GuildSettings settings;
    private String id;

    private Document muted;
    private Document banned;
    private Document embeds;
    private List<Document> commands = new ArrayList<>();
    private List<Document> streams = new ArrayList<>();
    private List<Document> ignoredRoles = new ArrayList<>();
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
        devi.getDatabaseManager().saveToDatabase("guilds", document, id);
    }

    @SuppressWarnings("Duplicates")
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

        MongoCollection<Document> commandCollection = devi.getDatabaseManager().getDatabase().getCollection("commands");
        commandCollection.find(Filters.eq("guild", this.id)).forEach((Consumer<? super Document>) command -> commands.add(command));

        MongoCollection<Document> streamsCollection = devi.getDatabaseManager().getDatabase().getCollection("streams");
        streamsCollection.find(Filters.eq("guild", this.id)).forEach((Consumer<? super Document>) stream -> streams.add(stream));

        MongoCollection<Document> autoModIgnoredRolesCollection = devi.getDatabaseManager().getDatabase().getCollection("ignored_roles");
        autoModIgnoredRolesCollection.find(Filters.eq("guild", this.id)).forEach((Consumer<? super Document>) ignoredRole -> ignoredRoles.add(ignoredRole));

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

        this.settings = guildSettings;
        this.ready = true;
    }

    void log(MessageEmbed embed) {
        TextChannel channel = devi.getShardManager().getTextChannelById(settings.getStringValue(GuildSettings.Settings.MOD_LOG_CHANNEL));
        if (channel != null) {
            MessageUtils.sendMessageAsync(channel, embed);
        }
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

    public List<Document> getIgnoredRoles() {
        return ignoredRoles;
    }

    public Devi getDevi() {
        return devi;
    }

    public List<Document> getCommands() {
        return commands;
    }

    public List<Document> getStreams() {
        return streams;
    }
}
