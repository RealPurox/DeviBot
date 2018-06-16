package me.purox.devi.commands.mod;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.Language;
import me.purox.devi.utils.DiscordUtils;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MuteCommandExecutor extends ListenerAdapter implements CommandExecutor {

    private Devi devi;

    public MuteCommandExecutor(Devi devi) {
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
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length < 2) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "mute <user> <reason>`"));
            return;
        }

        User user = DiscordUtils.getUser(args[0], command.getEvent().getGuild());
        if (user == null) {
            sender.reply(devi.getTranslation(command.getLanguage(), 13, "`" + args[0] + "`"));
            return;
        }
        Member member = command.getEvent().getGuild().getMember(user);

        if (command.getDeviGuild().getMuted().containsKey(user.getId())) {
            sender.reply(devi.getTranslation(command.getLanguage(), 26));
            return;
        }

        if (!PermissionUtil.canInteract(command.getEvent().getMember(), member) || user.getId().equals(sender.getId()) || command.getEvent().getJDA().getSelfUser().getId().equals(sender.getId())) {
            sender.reply(devi.getTranslation(command.getLanguage(), 27));
            return;
        }

        if (!PermissionUtil.checkPermission(command.getEvent().getGuild().getSelfMember(), Permission.MANAGE_ROLES) || !PermissionUtil.canInteract(command.getEvent().getGuild().getSelfMember(), member)) {
            sender.reply(devi.getTranslation(command.getLanguage(), 28));
            return;
        }

        Role role = command.getEvent().getGuild().getRoleById(command.getDeviGuild().getSettings().getStringValue(GuildSettings.Settings.MUTE_ROLE));
        if (role == null) {
            role = createMuteRole(command.getEvent().getGuild());
        }

        muteMember(Arrays.stream(args).skip(1).collect(Collectors.joining(" ")),
                sender.getName() + "#" + sender.getDiscriminator(),
                devi, command.getEvent().getGuild(), member, role, command.getEvent().getTextChannel());
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
                    MessageUtils.sendMessageAsync(channel, DeviEmote.MUTE.get() + " " + devi.getTranslation(language, 29, member.getUser().getName() + "#" + member.getUser().getDiscriminator()));
                },
                failure -> MessageUtils.sendMessageAsync(channel, devi.getTranslation(language, 30, member.getUser().getName() + "#" + member.getUser().getDiscriminator())));
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

    @Override
    public ModuleType getModuleType() {
        return ModuleType.MODERATION;
    }
}
