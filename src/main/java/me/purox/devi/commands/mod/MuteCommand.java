package me.purox.devi.commands.mod;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.Language;
import me.purox.devi.utils.DiscordUtils;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MuteCommand extends ListenerAdapter implements Command {

    private Devi devi;

    public MuteCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void onRoleDelete(RoleDeleteEvent event) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        if (event.getRole().getId().equals(deviGuild.getSettings().getStringValue(GuildSettings.Settings.MUTE_ROLE))) {
            deviGuild.getMuted().clear();
            deviGuild.saveSettings();
        }
    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        event.getRoles().forEach(role -> {
            if (role.getId().equals(deviGuild.getSettings().getStringValue(GuildSettings.Settings.MUTE_ROLE))) {
                if(deviGuild.getMuted().containsKey(event.getUser().getId())) {
                    deviGuild.getMuted().remove(event.getUser().getId());
                    deviGuild.saveSettings();
                }
            }
        });
    }

    @Override
    public void execute(String command, String[] args, MessageReceivedEvent event) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        String prefix = deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);

        if (args.length < 2) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 12, "`" + prefix + "mute <user> <reason>`"));
            return;
        }

        User user = DiscordUtils.getUser(args[0], event.getGuild());
        if (user == null) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 13, "`" + args[0] + "`"));
            return;
        }
        Member member = event.getGuild().getMember(user);

        if (deviGuild.getMuted().containsKey(user.getId())) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 26));
            return;
        }

        if (!PermissionUtil.canInteract(event.getMember(), member) || user.getId().equals(event.getAuthor().getId()) || event.getJDA().getSelfUser().getId().equals(event.getAuthor().getId())) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 27));
            return;
        }

        if (!PermissionUtil.checkPermission(event.getGuild().getSelfMember(), Permission.MANAGE_ROLES) || !PermissionUtil.canInteract(event.getGuild().getSelfMember(), member)) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 28));
            return;
        }

        Role role = event.getGuild().getRoleById(deviGuild.getSettings().getStringValue(GuildSettings.Settings.MUTE_ROLE));
        if (role == null) {
            role = createMuteRole(event.getGuild());
        }

        muteMember(Arrays.stream(args).skip(1).collect(Collectors.joining(" ")),
                event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(),
                devi, event.getGuild(), member, role, event.getTextChannel());
    }

    private Role createMuteRole(Guild guild) {
        DeviGuild deviGuild = devi.getDeviGuild(guild.getId());
        Role role = guild.getController().createRole().setName("Muted").complete();
        guild.getTextChannels().forEach(channel -> {
            if (PermissionUtil.checkPermission(channel, guild.getSelfMember(), Permission.MANAGE_PERMISSIONS)) {
                channel.createPermissionOverride(role).setDeny(Permission.MESSAGE_WRITE).queue();
            }
        });
        deviGuild.getSettings().setStringValue(GuildSettings.Settings.MUTE_ROLE, role.getId());
        return role;
    }

    private void muteMember(String reason, String punisher, Devi devi, Guild guild, Member member, Role muteRole, TextChannel channel) {
        DeviGuild deviGuild = devi.getDeviGuild(guild.getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        guild.getController().addRolesToMember(member, muteRole).queue(
                success-> {
                    Document punishment = new Document();
                    punishment.put("reason", reason);
                    punishment.put("punisher", punisher);

                    deviGuild.getMuted().put(member.getUser().getId(), punishment);
                    deviGuild.saveSettings();
                    devi.getModLogManager().logMute(deviGuild, member, punisher, reason);
                    MessageUtils.sendMessage(channel, DeviEmote.MUTE.get() + " " + devi.getTranslation(language, 29, member.getUser().getName() + "#" + member.getUser().getDiscriminator()));
                },
                failure -> MessageUtils.sendMessage(channel, devi.getTranslation(language, 30, member.getUser().getName() + "#" + member.getUser().getDiscriminator())));
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 41;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return Permission.VOICE_MUTE_OTHERS;
    }
}
