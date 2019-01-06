package net.devibot.provider.entities;

public enum ModuleType {

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
            if (moduleType.name().equalsIgnoreCase(input) || moduleType.getName().equalsIgnoreCase(input)) {
                return moduleType;
            }
        }
        return null;
    }

    public boolean canBeDisabled() {
        return canBeDisabled;
    }
}
