package me.purox.devi.utils;

import net.dv8tion.jda.core.entities.*;

public class DiscordUtils {

    public static Role getRole(String input, Guild guild) {
        //try to get role by id
        try {
            Long ID = Long.parseLong(input);
            if (guild.getRoleById(ID) != null)
                return guild.getRoleById(ID);
        } catch (NumberFormatException e) {}
        //try to get role by name
        if(!guild.getRolesByName(input, false).isEmpty())
            return guild.getRolesByName(input, false).get(0);
        //try to get role by mention
        if (input.startsWith("<@&") && input.endsWith(">")) {
            String id = input.substring(3, input.length() - 1);
            if (guild.getRoleById(id) != null) {
                return guild.getRoleById(id);
            }
        }
        return null;
    }

    public static TextChannel getTextChannel(String input, Guild guild) {
        //try to get channel by id
        try {
            Long ID = Long.parseLong(input);
            if (guild.getTextChannelById(ID) != null) {
                return guild.getTextChannelById(ID);
            }
        } catch (NumberFormatException e) {}
        //try to get channel by name
        if (!guild.getTextChannelsByName(input, false).isEmpty()) {
            return guild.getTextChannelsByName(input, false).get(0);
        }
        //try to get channel by mention
        if (input.startsWith("<#") && input.endsWith(">")) {
            String id = input.substring(2, input.length() - 1);
            if (guild.getTextChannelById(id) != null) {
                return guild.getTextChannelById(id);
            }
        }
        return null;
    }

    public static User getUser(String input, Guild guild) {
        //try to get user by id
        try {
            Long ID = Long.parseLong(input);
            if (guild.getMemberById(ID) != null) {
                return guild.getMemberById(input).getUser();
            }
        } catch (NumberFormatException e) {}
        //try to get user by name
        if (!guild.getMembersByName(input, false).isEmpty()) {
            return guild.getMembersByName(input, false).get(0).getUser();
        }
        //try to get user by nickname
        if(!guild.getMembersByNickname(input, false).isEmpty()) {
            return guild.getMembersByNickname(input, false).get(0).getUser();
        }
        //try to get user by mention
        if (input.startsWith("<@") && input.endsWith(">")) {
            String id = input.substring(input.startsWith("<@!") ? 3 : 2, input.length() - 1);
            Member member = guild.getMemberById(id);
            if (member != null) {
                return member.getUser();
            }
        }
        //user not found, return null
        return null;
    }
}
