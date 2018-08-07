package me.purox.devi.core;

public enum ModuleType {

    MANAGEMENT_COMMANDS(true),
    INFO_COMMANDS(false),
    MODERATION(true),
    MUSIC(true),
    GAME_COMMANDS(true),
    FUN_COMMANDS(true),
    TWITCH(true),
    NSFW_COMMANDS(true),
    DEV(false);

    private boolean canBeDisabled;

    ModuleType(boolean canBeDisabled) {
        this.canBeDisabled = canBeDisabled;
    }

    public String getName() {
        StringBuilder name = new StringBuilder();
        String raw = this.name().toLowerCase();
        String[] rawSplit = raw.split("_");

        for (String s : rawSplit) {
            char capital = Character.toUpperCase(s.charAt(0));
            name.append(capital).append(s.substring(1)).append(" ");
        }

        return name.toString().substring(0, name.toString().length() - 1);
    }

    public static ModuleType getByName(String input) {
        for (ModuleType moduleType : values()) {
            if (moduleType.name().toLowerCase().equalsIgnoreCase(input) || moduleType.getName().toLowerCase().equalsIgnoreCase(input)) {
                return moduleType;
            }
        }
        return null;
    }

    public boolean canBeDisabled() {
        return canBeDisabled;
    }
}
