package me.purox.devi.entities;

public enum Language {

    ENGLISH("eng"), ITALIAN("ita"), GERMAN("ger"), SPANISH("esp"), SWEDISH("swe"), NORWEGIAN("nor"), TURKISH("trk"), /*PORTUGUESE("prt")*/, FRENCH("fra"), /*HUNGARIAN("hun")*/;

    private String registry;
    Language(String registry) {
        this.registry = registry;
    }

    public String getRegistry() {
        return registry;
    }

    public String getName() {
        char capital = Character.toUpperCase(this.name().charAt(0));
        return capital + this.name().toLowerCase().substring(1);
    }

    public static Language getLanguage(String input) {
        for (Language language : Language.values()) {
            if(language.name().equalsIgnoreCase(input) || language.getRegistry().equalsIgnoreCase(input)) {
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
}
