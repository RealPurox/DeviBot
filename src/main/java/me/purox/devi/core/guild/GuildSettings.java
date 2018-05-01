package me.purox.devi.core.guild;
import me.purox.devi.core.DeviEmote;

import java.util.HashMap;

public class GuildSettings {

    private HashMap<Settings, String> stringOptions = new HashMap<>();
    private HashMap<Settings, Boolean> booleanOptions = new HashMap<>();
    private HashMap<Settings, Integer> integerOptions = new HashMap<>();

    private DeviGuild deviGuild;

    public GuildSettings(DeviGuild deviGuild) {
        this.deviGuild = deviGuild;
    }

    public enum Settings {
        PREFIX("!", ":page_with_curl:", 5, true),
        LANGUAGE("english", ":earth_americas:", 6, true),
        MUTE_ROLE("-1", ":mute:", 24, false),
        MOD_LOG_CHANNEL("-1", ":microphone2:", 59, false),
        MOD_LOG_ENABLED(false, ":newspaper:", 60, false),
        MOD_LOG_MUTES(true, DeviEmote.MUTE.get(), 71, false),
        MOD_LOG_BANS(true, DeviEmote.BAN.get(), 72, false),
        AUTO_MOD_ENABLED(true, DeviEmote.BAN_HAMMER.get(), 76, false),
        AUTO_MOD_ANTI_ADS(true, DeviEmote.ADVERTISEMENT.get(), 77, false);

        private Integer translationID;
        private String emoji;
        private boolean editable;

        private String defaultStringValue;
        private Boolean defaultBooleanValue;
        private Integer defaultIntegerValue;

        Settings (String defaultStringValue, String emoji, Integer translationID, boolean editable) {
            this.defaultStringValue = defaultStringValue;
            this.emoji = emoji;
            this.translationID = translationID;
            this.editable = editable;
        }

        Settings (Boolean defaultBooleanValue, String emoji, Integer translationID, boolean editable) {
            this.defaultBooleanValue = defaultBooleanValue;
            this.emoji = emoji;
            this.translationID = translationID;
            this.editable = editable;
        }

        Settings (Integer defaultIntegerValue, String emoji, Integer translationID, boolean editable) {
            this.defaultIntegerValue = defaultIntegerValue;
            this.emoji = emoji;
            this.translationID = translationID;
            this.editable = editable;
        }

        public String getEmoji() {
            return emoji;
        }

        public Integer getTranslationID() {
            return translationID;
        }

        public boolean isEditable() {
            return editable;
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
                if(settings.isEditable()) {
                    if (settings.name().equalsIgnoreCase(input)) {
                        return settings;
                    }
                }
            }
            return null;
        }
    }

    private void logSettingUpdate(Settings settings, Object value) {
        System.out.println("[INFO] Set " + settings.name() + " to " + value + " in guild " + deviGuild.getId());
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
