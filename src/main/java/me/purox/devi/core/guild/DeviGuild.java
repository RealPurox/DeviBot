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
                guildSettings.setStringValue(setting, document.getString(setting.name().toLowerCase()) == null ?
                        (String) setting.getDefaultValue() : document.getString(setting.name().toLowerCase()));
            } else if (setting.isBooleanValue()) {
                guildSettings.setBooleanValue(setting, document.getBoolean(setting.name().toLowerCase()) == null ?
                        (boolean) setting.getDefaultValue() : document.getBoolean(setting.name().toLowerCase()));
            } else if (setting.isIntegerValue()) {
                guildSettings.setIntegerValue(setting, document.getInteger(setting.name().toLowerCase()) == null ?
                        (int) setting.getDefaultValue() : document.getInteger(setting.name().toLowerCase()));
            }
        }

        MongoCollection<Document> commandCollection = devi.getDatabaseManager().getDatabase().getCollection("commands");
        commandCollection.find(Filters.eq("guild", this.id)).forEach((Consumer<? super Document>) command -> commands.add(command));

        MongoCollection<Document> streamsCollection = devi.getDatabaseManager().getDatabase().getCollection("streams");
        streamsCollection.find(Filters.eq("guild", this.id)).forEach((Consumer<? super Document>) stream -> streams.add(stream));

        MongoCollection<Document> autoModIgnoredRolesCollection = devi.getDatabaseManager().getDatabase().getCollection("ignored_roles");
        autoModIgnoredRolesCollection.find(Filters.eq("guild", this.id)).forEach((Consumer<? super Document>) ignoredRole -> ignoredRoles.add(ignoredRole));

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

    boolean isReady() {
        return ready;
    }

    public String getId() {
        return id;
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
