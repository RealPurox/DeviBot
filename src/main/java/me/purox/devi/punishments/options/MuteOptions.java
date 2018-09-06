package me.purox.devi.punishments.options;

import net.dv8tion.jda.core.entities.Role;

public class MuteOptions implements Options{

    private Role role;

    public MuteOptions setRole(Role role) {
        this.role = role;
        return this;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "Role: " + role;
    }
}
