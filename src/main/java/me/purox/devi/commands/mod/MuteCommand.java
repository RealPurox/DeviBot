package me.purox.devi.commands.mod;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.waiter.WaiterVoid;
import me.purox.devi.punishments.Punishment;
import me.purox.devi.punishments.PunishmentBuilder;
import me.purox.devi.punishments.options.MuteOptions;
import me.purox.devi.utils.DiscordUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MuteCommand extends ICommand {

    private Devi devi;

    public MuteCommand(Devi devi) {
        super("mute");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        String[] args = command.getArgs();

        if(args.length < 1){
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "mute <user> [reason]`\n\n" +
                    devi.getTranslation(command.getLanguage(), 607, "`[reason]`")));
            return;
        }

        User user = DiscordUtils.getUser(args[0], command.getGuild());
        if (user == null) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 13, "`" + args[0] + "`"));
            return;
        }
        Member member = command.getGuild().getMember(user);

        if (!PermissionUtil.canInteract(command.getMember(), member) || user.getId().equals(sender.getId()) ||
                command.getJDA().getSelfUser().getId().equals(sender.getId())) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 27));
            return;
        }

        if (!PermissionUtil.checkPermission(command.getGuild().getSelfMember(), Permission.MANAGE_ROLES) ||
                !PermissionUtil.canInteract(command.getGuild().getSelfMember(), member)) {
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

        Role role = command.getGuild().getRoleById(command.getDeviGuild().getSettings().getStringValue(GuildSettings.Settings.MUTE_ROLE));

        if (role == null) {
            command.getGuild().getController().createRole().setMentionable(false).setColor(Color.decode("#7289DA"))
                    .setName("Muted").queue(muteRole -> {
                command.getGuild().getController().modifyRolePositions().selectPosition(muteRole)
                        .moveTo(command.getGuild().getSelfMember().getRoles().get(0).getPosition() - 1).queue();
                command.getGuild().getTextChannels().forEach(channel -> {
                    if (PermissionUtil.checkPermission(channel, command.getGuild().getSelfMember(), Permission.MANAGE_PERMISSIONS)) {
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
}
