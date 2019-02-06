package net.devibot.core.entities;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public enum Language {

    ENGLISH("eng", "\uD83C\uDDEC\uD83C\uDDE7", "language.english"),
    ITALIAN("ita", "\uD83C\uDDEE\uD83C\uDDF9", "language.italian"),
    GERMAN("ger", "\uD83C\uDDE9\uD83C\uDDEA", "language.german"),
    SPANISH("esp", "\uD83C\uDDEA\uD83C\uDDF8", "language.spanish"),
    SWEDISH("swe", "\uD83C\uDDF8\uD83C\uDDEA", "language.swedish"),
    TURKISH("trk", "\uD83C\uDDF9\uD83C\uDDF7", "language.turkish"),
    FRENCH("fra", "\uD83C\uDDEB\uD83C\uDDF7", "language.french"),
    NORWEGIAN("nor", "\uD83C\uDDF3\uD83C\uDDF4", "language.norwegian");

    private String registry;
    private String emojiFlag;
    private String translationId;

    Language(String registry, String emojiFlag, String translationId) {
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

    public String getTranslationKey() {
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
        return null;
    }

    public static Collection<String> getAllLanguageFlags() {
        return Arrays.stream(values()).map(Language::getEmojiFlag).collect(Collectors.toList());
    }
}
