package me.purox.devi.commands.mod;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.punishments.Punishment;
import me.purox.devi.punishments.PunishmentBuilder;
import me.purox.devi.utils.DiscordUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.core.requests.restaction.RoleAction;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MuteCommandExecutor extends ListenerAdapter implements CommandExecutor {

    private Devi devi;

    public MuteCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if(args.length < 1){
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "mute <user> [reason]`\n\n" +
                    devi.getTranslation(command.getLanguage(), 607, "`[reason]`")));
            return;
        }

        User user = DiscordUtils.getUser(args[0], command.getEvent().getGuild());
        if (user == null) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 13, "`" + args[0] + "`"));
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
        //todo
        if (role == null) {}

        String reason = args.length == 1 ? "Unknown reason" : Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        new PunishmentBuilder(command.getDeviGuild())
                .setType(Punishment.Type.MUTE)
                .setReason(reason);


        //muteMember(Arrays.stream(args).skip(1).collect(Collectors.joining(" ")),
          //      sender.getName() + "#" + sender.getDiscriminator(),
            //    devi, command.getEvent().getGuild(), member, role, command.getEvent().getTextChannel());
    }

    private RoleAction createMuteRole(Guild guild) {
        DeviGuild deviGuild = devi.getDeviGuild(guild.getId());
        return guild.getController().createRole().setName("Muted").setColor(Color.decode("#7289DA")).setMentionable(false).setHoisted(true);
    }

    private void updateRole(Role role, Guild guild) {
        guild.getTextChannels().forEach(channel -> {
            if (PermissionUtil.checkPermission(channel, guild.getSelfMember(), Permission.MANAGE_PERMISSIONS)) {
                channel.createPermissionOverride(role).setDeny(Permission.MESSAGE_WRITE).queue();
            }
        });
        devi.getDeviGuild(guild.getId()).getSettings().setStringValue(GuildSettings.Settings.MUTE_ROLE, role.getId());
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
