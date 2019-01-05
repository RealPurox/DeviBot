package net.devibot.provider.entities;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public enum Language {

    ENGLISH("eng", "\uD83C\uDDEC\uD83C\uDDE7", 163),
    ITALIAN("ita", "\uD83C\uDDEE\uD83C\uDDF9", 164),
    GERMAN("ger", "\uD83C\uDDE9\uD83C\uDDEA", 165),
    SPANISH("esp", "\uD83C\uDDEA\uD83C\uDDF8", 166),
    SWEDISH("swe", "\uD83C\uDDF8\uD83C\uDDEA", 167),
    TURKISH("trk", "\uD83C\uDDF9\uD83C\uDDF7", 168),
    FRENCH("fra", "\uD83C\uDDEB\uD83C\uDDF7", 169),
    NORWEGIAN("nor", "\uD83C\uDDF3\uD83C\uDDF4", 624),
    UNKNOWN("unknown", "", -1);

    private String registry;
    private String emojiFlag;
    private int translationId;

    Language(String registry, String emojiFlag, int translationId) {
        this.registry = registry;
        this.emojiFlag = emojiFlag;
        this.translationId = translationId;
    }

    public String getRegistry() {
        return registry;
    }

    public String getEmojiFlag() {
        return emojiFlag;
    }

    public int getTranslationId() {
        return translationId;
    }

    public String getName() {
        char capital = Character.toUpperCase(this.name().charAt(0));
        return capital + this.name().toLowerCase().substring(1);
    }

    public static Language getLanguage(String input) {
        for (Language lang : Language.values()) {
            if (lang.getName().equalsIgnoreCase(input) || lang.getRegistry().equalsIgnoreCase(input) || lang.getEmojiFlag().equalsIgnoreCase(input))
                return lang;
        }
        return UNKNOWN;
    }

    public static Collection<String> getAllLangaugeFlags() {
        return Arrays.stream(values()).map(Language::getEmojiFlag).collect(Collectors.toList());
    }
}
