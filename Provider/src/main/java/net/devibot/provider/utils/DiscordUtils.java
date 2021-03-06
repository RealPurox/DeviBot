package net.devibot.provider.utils;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiscordUtils {

    public static Role getRole(String input, Guild guild) {
        //try to get role by id
        try {
            long ID = Long.parseLong(input);
            if (guild.getRoleById(ID) != null)
                return guild.getRoleById(ID);
        } catch (NumberFormatException ignored) {}
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
        //try to get by name again, but don't care about case sensitivity now
        if(!guild.getRolesByName(input, true).isEmpty())
            return guild.getRolesByName(input, true).get(0);
        return null; //not found
    }

    public static TextChannel getTextChannel(String input, Guild guild) {
        //try to get channel by id
        try {
            long ID = Long.parseLong(input);
            if (guild.getTextChannelById(ID) != null) {
                return guild.getTextChannelById(ID);
            }
        } catch (NumberFormatException ignored) {}
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
        //try to get by name again, but don't care about case sensitivity now
        if (!guild.getTextChannelsByName(input, true).isEmpty()) {
            return guild.getTextChannelsByName(input, true).get(0);
        }
        return null; //not found
    }

    public static User getUser(String input, Guild guild) {
        //try to get user by id
        try {
            long ID = Long.parseLong(input);
            if (guild.getMemberById(ID) != null) {
                return guild.getMemberById(input).getUser();
            }
        } catch (NumberFormatException ignored) {}
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
        //try to get by name again, but don't care about case sensitivity now
        if (!guild.getMembersByName(input, true).isEmpty()) {
            return guild.getMembersByName(input, true).get(0).getUser();
        }
        //try to get user by nickname again, but don't care about case sensitivity now
        if(!guild.getMembersByNickname(input, true).isEmpty()) {
            return guild.getMembersByNickname(input, true).get(0).getUser();
        }
        return null; //not found
    }

    public static List<Member> getGuildMembersWithPermissions(Guild guild, Permission ... permissions) {
        List<Member> members = new ArrayList<>();
        for (Member member : guild.getMembers()) {
            if (member.hasPermission(permissions))
                members.add(member);
        }
        return members;
    }

    public static List<Member> getGuildMembersWithAnyRole(Guild guild, Role ... roles) {
        List<Member> members = new ArrayList<>();
        for (Member member : guild.getMembers()) {
            for (Role role : roles) {
                if (member.getRoles().contains(role))
                    members.add(member);
            }
        }
        return members;
    }

    public static List<Member> getGuildMemberWithAllRoles(Guild guild, Role ... roles) {
        List<Member> members = new ArrayList<>();
        for (Member member : guild.getMembers()) {
            if (member.getRoles().containsAll(Arrays.asList(roles)))
                members.add(member);
        }
        return members;
    }

}
