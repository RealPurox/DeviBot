package me.purox.devi.commands.mod;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.punishments.Punishment;
import me.purox.devi.punishments.PunishmentBuilder;
import me.purox.devi.utils.DiscordUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.Arrays;
import java.util.stream.Collectors;

public class KickCommand extends ICommand {

    private Devi devi;

    public KickCommand(Devi devi) {
        super("kick");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        String[] args = command.getArgs();

        if(args.length < 1){
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "kick <user> [reason]`\n\n" +
                    devi.getTranslation(command.getLanguage(), 605, "`[reason]`")));
            return;
        }

        User user = DiscordUtils.getUser(args[0], command.getGuild());
        if (user == null) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 13, "`" + args[0] + "`"));
            return;
        }

        Member member = command.getGuild().getMember(user);
        if (!PermissionUtil.canInteract(command.getMember(), member) || user.getId().equals(sender.getId()) || command.getJDA().getSelfUser().getId().equals(sender.getId())) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 528));
            return;
        }

        if (sender.getId().equalsIgnoreCase(user.getId()) ||
                !PermissionUtil.checkPermission(command.getGuild().getSelfMember(), Permission.KICK_MEMBERS) ||
                !PermissionUtil.canInteract(command.getGuild().getSelfMember(), member)) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 529));
            return;
        }

        String reason = args.length == 1 ? "Unknown reason" : Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        new PunishmentBuilder(command.getDeviGuild())
                .setReason(reason)
                .setType(Punishment.Type.KICK)
                .setPunished(user)
                .setPunisher(sender)
                .build().execute(success -> sender.reply(Emote.SUCCESS + " | " + devi.getTranslation(command.getLanguage(), 606, "`" + user.getName() + "#" + user.getDiscriminator() + "`")),
                error -> sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 531)));

    }
}
