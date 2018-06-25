package me.purox.devi.core.guild;

import me.purox.devi.core.DeviEmote;
import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;

public class GuildSettings {

    private HashMap<Settings, String>  stringOptions  = new HashMap<>();
    private HashMap<Settings, Boolean> booleanOptions = new HashMap<>();
    private HashMap<Settings, Integer> integerOptions = new HashMap<>();

    private DeviGuild deviGuild;

    public GuildSettings(DeviGuild deviGuild) {
        this.deviGuild = deviGuild;
    }

    public enum Settings {
        PREFIX                    ( "!",        ":page_with_curl:",            5,   "prefix"                , true ),

        LANGUAGE                  ( "english",  ":earth_americas:",            6,   "language"              , true ),

        MUTE_ROLE                 ( "-1",       ":mute:",                      24,  "muterole"              , false),

        MOD_LOG_ENABLED           ( false,      ":newspaper:",                 69,  "modlog"                , true ),
        MOD_LOG_CHANNEL           ( "-1",       ":microphone2:",               59,  "modlog channel"        , false),
        MOD_LOG_MUTES             ( true,       DeviEmote.MUTE.get(),          71,  "modlog mutes"          , false),
        MOD_LOG_BANS              ( true,       DeviEmote.BAN.get(),           72,  "modlog bans"           , false),
        MOD_LOG_MESSAGE_EDITED    ( true,       ":pen_ballpoint:",             178, "modlog message-edit"   , false),
        MOD_LOG_MESSAGE_DELETED   ( true,       ":no_entry_sign:",             179, "modlog message-delete" , false),

        AUTO_MOD_ENABLED          ( true,       ":hammer_pick:",               301, "automod"               , true ),
        AUTO_MOD_ANTI_ADS         ( true,       ":tv:",                        77,  "automod ads"           , false),
        AUTO_MOD_ANTI_CAPS        ( true,       ":ab:",                        81,  "automod caps"          , false),
        AUTO_MOD_ANTI_EMOJI       ( true,       ":stuck_out_tongue:",          161, "automod emoji"         , false),

        MUSIC_LOG_ENABLED         ( true,       ":checkered_flag:",            84,  "musiclog"              , true ),
        MUSIC_LOG_CHANNEL         ( "-1",       ":notes:",                     83,  "musiclog channel"      , false),

        TWITCH_CHANNEL            ( "-1",       DeviEmote.TWITCH.get(),        198, "twitch"                , true ),

        WELCOMER_ENABLED          ( true,       ":wave",                       373, "welcome"               , false),
        JOIN_MESSAGE              ( "Hey {user}, welcome to {server}. :wave:",
                                                DeviEmote.SUCCESS.get(),       372, "welcome join"          , false),
        LEAVE_MESSAGE             ( "**{user}** left {server}.",
                                                DeviEmote.ERROR.get(),         375, "welcome leave"         , false),
        AUTO_ROLE_ENABLED         ( true,       ":question:",                  374, "welcome role"          , false),
        AUTO_ROLE                 ( "-1",       ":robot:",                     374, "welcome role"          , false);


        private Integer translationID;
        private String emoji;
        private String command;
        private boolean displayInSettings;

        private String defaultStringValue;
        private Boolean defaultBooleanValue;
        private Integer defaultIntegerValue;


        Settings (String defaultStringValue, String emoji, Integer translationID, String command, boolean displayInSettings) {
            this.defaultStringValue = defaultStringValue;
            this.emoji = emoji;
            this.translationID = translationID;
            this.command = command;
            this.displayInSettings = displayInSettings;
        }

        Settings (Boolean defaultBooleanValue, String emoji, Integer translationID, String command, boolean displayInSettings) {
            this.defaultBooleanValue = defaultBooleanValue;
            this.emoji = emoji;
            this.translationID = translationID;
            this.command = command;
            this.displayInSettings = displayInSettings;
        }

        Settings (Integer defaultIntegerValue, String emoji, Integer translationID, String command, boolean displayInSettings) {
            this.defaultIntegerValue = defaultIntegerValue;
            this.emoji = emoji;
            this.translationID = translationID;
            this.command = command;
            this.displayInSettings = displayInSettings;
        }

        public String getEmoji() {
            return emoji;
        }

        public Integer getTranslationID() {
            return translationID;
        }

        public String getCommand() {
            return command;
        }

        public Object getDefaultValue() {
            if (defaultBooleanValue != null) {
                return defaultBooleanValue;
            } else if (defaultStringValue != null) {
                return defaultStringValue;
            } else if (defaultIntegerValue != null) {
                return defaultIntegerValue;
            }
            return null;
        }

        public boolean isStringValue() {
            return defaultStringValue != null;
        }

        public boolean isBooleanValue() {
            return defaultBooleanValue != null;
        }

        public boolean isIntegerValue() {
            return defaultIntegerValue != null;
        }


        public static Settings getSetting(String input) {
            for (Settings settings : values()) {
                if (settings.name().equalsIgnoreCase(input)) {
                    return settings;
                }
            }
            return null;
        }

        public boolean isDisplayedInSettings() {
            return displayInSettings;
        }

        public String getName() {
            StringBuilder rawName = new StringBuilder();
            String raw = this.name().toLowerCase();
            String[] rawSplit = raw.split("_");

            for (String s : rawSplit) {
                char capital = Character.toUpperCase(s.charAt(0));
                rawName.append(capital).append(s.substring(1)).append(" ");
            }

            String name = rawName.toString().substring(0, rawName.toString().length() - 1);
            if (name.endsWith(" Enabled")) name = name.substring(0, name.length() - 8);
            return name;
        }
    }

    private void logSettingUpdate(Settings settings, Object value) {
        Guild guild = deviGuild.getDevi().getShardManager().getGuildById(deviGuild.getId());
        deviGuild.getDevi().getLogger().log("Set " + settings.name() + " to " + value + " in guild " + deviGuild.getId() + " (" + (guild == null ? "N/A" : guild.getName()) + ")");
    }

    public void setStringValue(Settings settings, String value) {
        stringOptions.put(settings, value);
        if(deviGuild.isReady()) {
            deviGuild.saveSettings();
            logSettingUpdate(settings, value);
        }
    }

    public void setBooleanValue(Settings settings, boolean value) {
        booleanOptions.put(settings, value);
        if(deviGuild.isReady()) {
            deviGuild.saveSettings();
            logSettingUpdate(settings, value);
        }
    }

    void setIntegerValue(Settings settings, int value) {
        integerOptions.put(settings, value);
        if(deviGuild.isReady()) {
            deviGuild.saveSettings();
            logSettingUpdate(settings, value);
        }
    }

    public String getStringValue(Settings settings) {
        if(!settings.isStringValue()) return null;
        return stringOptions.get(settings);
    }

    public Boolean getBooleanValue(Settings settings) {
        if(!settings.isBooleanValue()) return null;
        return booleanOptions.get(settings);
    }

    public Integer getIntegerValue(Settings settings) {
        if(!settings.isIntegerValue()) return null;
        return integerOptions.get(settings);
    }

    public Object getValue(Settings settings) {
        if (stringOptions.containsKey(settings)) {
            return stringOptions.get(settings);
        } else if (booleanOptions.containsKey(settings)) {
            return booleanOptions.get(settings);
        } else if (integerOptions.containsKey(settings)) {
            return integerOptions.get(settings);
        }
        return null;
    }

    public DeviGuild getDeviGuild() {
        return deviGuild;
    }
}
