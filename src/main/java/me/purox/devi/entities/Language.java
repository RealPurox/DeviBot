package me.purox.devi.entities;

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
    NORWEGIAN("nor", "\uD83C\uDDF3\uD83C\uDDF4", 624);

    private String registry;
    private String flag;
    private int translationId;

    Language(String registry, String flag, int translationId) {
        this.registry = registry;
        this.flag = flag;
        this.translationId = translationId;
    }

    public String getRegistry() {
        return registry;
    }

    public String getFlag() {
        return flag;
    }

    public int getTranslationId() {
        return translationId;
    }

    public String getName() {
        char capital = Character.toUpperCase(this.name().charAt(0));
        return capital + this.name().toLowerCase().substring(1);
    }

    public static Language getLanguage(String input) {
        for (Language language : Language.values()) {
            if(language.name().equalsIgnoreCase(input) || language.getRegistry().equalsIgnoreCase(input) || language.getFlag().equalsIgnoreCase(input)) {
                return language;
            }
        }
        return null;
    }

    public static String getAllLanguagesBeautiful() {
        StringBuilder sb = new StringBuilder();
        for (Language language : values()) {
            char capital = Character.toUpperCase(language.name().charAt(0));
            sb.append(capital).append(language.name().toLowerCase().substring(1)).append(", ");
        }
        return sb.toString().substring(0, sb.toString().length() - 2);
    }

    public static Collection<String> getAllLanguageFlags() {
        return Arrays.stream(values()).map(Language::getFlag).collect(Collectors.toList());
    }
}
