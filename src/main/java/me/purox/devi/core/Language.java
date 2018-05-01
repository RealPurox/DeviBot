package me.purox.devi.core;

public enum Language {

    ENGLISH("eng"), ITALIAN("ita"), GERMAN("ger"), SPANISH("esp"), TURKISH("trk");

    private String registry;
    Language(String registry) {
        this.registry = registry;
    }

    public String getRegistry() {
        return registry;
    }

    public static Language getLanguage(String input) {
        for (Language language : Language.values()) {
            if(language.name().equalsIgnoreCase(input) || language.getRegistry().equalsIgnoreCase(input)) {
                return language;
            }
        }
        return null;
    }
}
