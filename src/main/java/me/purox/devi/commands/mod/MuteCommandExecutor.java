package me.purox.devi.commands.mod;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.waiter.WaiterVoid;
import me.purox.devi.punishments.Punishment;
import me.purox.devi.punishments.PunishmentBuilder;
import me.purox.devi.punishments.options.MuteOptions;
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
import java.util.concurrent.atomic.AtomicBoolean;
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

        if (!PermissionUtil.canInteract(command.getEvent().getMember(), member) || user.getId().equals(sender.getId()) ||
                command.getEvent().getJDA().getSelfUser().getId().equals(sender.getId())) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 27));
            return;
        }

        if (!PermissionUtil.checkPermission(command.getEvent().getGuild().getSelfMember(), Permission.MANAGE_ROLES) ||
                !PermissionUtil.canInteract(command.getEvent().getGuild().getSelfMember(), member)) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 28));
            return;
        }


        String reason = args.length == 1 ? "Unknown reason" : Arrays.stream(args).skip(1).collect(Collectors.joining(" "));

        WaiterVoid action = (r -> {
            if (r == null) throw new UnsupportedOperationException("No Role Provided!!!!");
            new PunishmentBuilder(command.getDeviGuild())
                    .setReason(reason)
                    .setType(Punishment.Type.MUTE)
                    .setPunished(user)
                    .setPunisher(sender)
                    .setOptions(new MuteOptions().setRole((Role)r))
                    .build().execute(success -> sender.reply(Emote.SUCCESS + " | " + devi.getTranslation(command.getLanguage(), 181,
                    "`" + user.getName() + "#" + user.getDiscriminator() + "`")),
                    error -> sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 30, "`" + user.getName() + "#" + user.getDiscriminator() + "`")));
        });

        Role role = command.getEvent().getGuild().getRoleById(command.getDeviGuild().getSettings().getStringValue(GuildSettings.Settings.MUTE_ROLE));

        if (role == null) {
            command.getEvent().getGuild().getController().createRole().setMentionable(false).setColor(Color.decode("#7289DA"))
                    .setName("Muted").queue(muteRole -> {
                command.getEvent().getGuild().getController().modifyRolePositions().selectPosition(muteRole)
                        .moveTo(command.getEvent().getGuild().getSelfMember().getRoles().get(0).getPosition() - 1).queue();
                command.getEvent().getGuild().getTextChannels().forEach(channel -> {
                    if (PermissionUtil.checkPermission(channel, command.getEvent().getGuild().getSelfMember(), Permission.MANAGE_PERMISSIONS)) {
                        channel.createPermissionOverride(muteRole).setDeny(Permission.MESSAGE_WRITE).queue();
                    }
                });
                command.getDeviGuild().getSettings().setStringValue(GuildSettings.Settings.MUTE_ROLE, muteRole.getId());
                action.run(muteRole);
            }, error -> sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 30, "`" + user.getName() + "#" + user.getDiscriminator() + "`")));
        } else {
            String roleId = command.getDeviGuild().getSettings().getStringValue(GuildSettings.Settings.MUTE_ROLE);
            for (Role memberRole : member.getRoles()) {
                if (memberRole.getId().equals(roleId)) {
                    sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 26));
                    return;
                }
            }
            action.run(role);
        }
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
