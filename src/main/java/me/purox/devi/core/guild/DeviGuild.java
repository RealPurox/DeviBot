package me.purox.devi.core.guild;

import com.mongodb.util.JSON;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.entities.Command;
import me.purox.devi.core.guild.entities.IgnoredRole;
import me.purox.devi.core.guild.entities.Punishment;
import me.purox.devi.core.guild.entities.Stream;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeviGuild {

    private Devi devi;
    private GuildSettings settings;
    private String id;

    private List<Command> commandEntities = new ArrayList<>();
    private List<Stream> streams = new ArrayList<>();
    private List<IgnoredRole> ignoredRoles = new ArrayList<>();
    private List<Punishment> punishments = new ArrayList<>();
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

        this.commandEntities = getEntities(Command.class, "commands");
        this.streams = getEntities(Stream.class, "streams");
        this.ignoredRoles = getEntities(IgnoredRole.class, "ignored_roles");
        this.punishments = getEntities(Punishment.class, "punishments");

        this.settings = guildSettings;
        this.ready = true;
    }

    public void log(MessageEmbed embed) {
        TextChannel channel = devi.getShardManager().getTextChannelById(settings.getStringValue(GuildSettings.Settings.MOD_LOG_CHANNEL));
        if (channel != null) {
            MessageUtils.sendMessageAsync(channel, embed);
        }
    }

    private <T> List<T> getEntities(Class<T> clazz, String collection) {
        List<Document> data = devi.getDatabaseManager().getDocuments("guild", id, collection);
        List<T> entities = new ArrayList<>();

        for (Document document : data) {
            JSONObject jsonData = document == null ? new JSONObject() : new JSONObject(JSON.serialize(document));
            entities.add(Devi.GSON.fromJson(jsonData.toString(), clazz));
        }

        return entities;
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

    public List<IgnoredRole> getIgnoredRoles() {
        return ignoredRoles;
    }

    public List<Command> getCommandEntities() {
        return commandEntities;
    }

    public List<Stream> getStreams() {
        return streams;
    }

    public List<Punishment> getPunishments() {
        return punishments;
    }

    public Devi getDevi() {
        return devi;
    }
}
