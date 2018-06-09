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
        PREFIX                  ( "!",        ":page_with_curl:",            5,   "prefix"                ),
        LANGUAGE                ( "english",  ":earth_americas:",            6,   "language"              ),
        MUTE_ROLE               ( "-1",       ":mute:",                      24,  "muterole"              ),
        MOD_LOG_ENABLED         ( false,      ":newspaper:",                 69,  "modlog"                ),
        MOD_LOG_CHANNEL         ( "-1",       ":microphone2:",               59,  "modlog channel"        ),
        MOD_LOG_MUTES           ( true,       DeviEmote.MUTE.get(),          71,  "modlog mutes"          ),
        MOD_LOG_BANS            ( true,       DeviEmote.BAN.get(),           72,  "modlog bans"           ),
        MOD_LOG_MESSAGE_EDITED  ( true,       ":pen_ballpoint:",             178, "modlog message-edit"   ),
        MOD_LOG_MESSAGE_DELETED ( true,       ":no_entry_sign:",             179, "modlog message-delete" ),
        AUTO_MOD_ENABLED        ( true,       ":hammer_pick:",               301,  "automod"               ),
        AUTO_MOD_ANTI_ADS       ( true,       ":tv:",                        77,  "automod ads"           ),
        AUTO_MOD_ANTI_CAPS      ( true,       ":ab:",                        81,  "automod caps"          ),
        AUTO_MOD_ANTI_EMOJI     ( true,       ":stuck_out_tongue:",          161, "automod emoji"         ),
        MUSIC_LOG_ENABLED       ( true,       ":checkered_flag:",            84,  "musiclog"              ),
        MUSIC_LOG_CHANNEL       ( "-1",       ":notes:",                     83,  "musiclog channel"      ),
        TWITCH_CHANNEL          ( "-1",       DeviEmote.TWITCH.get(),        198, "twitch"                );

        private Integer translationID;
        private String emoji;
        private String command;

        private String defaultStringValue;
        private Boolean defaultBooleanValue;
        private Integer defaultIntegerValue;

        Settings (String defaultStringValue, String emoji, Integer translationID, String command) {
            this.defaultStringValue = defaultStringValue;
            this.emoji = emoji;
            this.translationID = translationID;
            this.command = command;
        }

        Settings (Boolean defaultBooleanValue, String emoji, Integer translationID, String command) {
            this.defaultBooleanValue = defaultBooleanValue;
            this.emoji = emoji;
            this.translationID = translationID;
            this.command = command;
        }

        Settings (Integer defaultIntegerValue, String emoji, Integer translationID, String command) {
            this.defaultIntegerValue = defaultIntegerValue;
            this.emoji = emoji;
            this.translationID = translationID;
            this.command = command;
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
    }

    private void logSettingUpdate(Settings settings, Object value) {
        Guild guild = deviGuild.getDevi().getShardManager().getGuildById(deviGuild.getId());
        System.out.println("[INFO] Set " + settings.name() + " to " + value + " in guild " + deviGuild.getId() + " (" + (guild == null ? "N/A" : guild.getName()) + ")");
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

    public void setIntegerValue(Settings settings, int value) {
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
