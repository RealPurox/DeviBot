package me.purox.devi.commands.mod;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.punishments.Punishment;
import me.purox.devi.punishments.PunishmentBuilder;
import me.purox.devi.punishments.options.BanOptions;
import me.purox.devi.utils.DiscordUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.Arrays;
import java.util.stream.Collectors;

public class BanCommand extends ICommand {

    private Devi devi;

    public BanCommand(Devi devi) {
        super("ban");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        String[] args = command.getArgs();

        if (args.length < 1) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "ban <user> [days] [reason]`\n\n" +
                    devi.getTranslation(command.getLanguage(), 597, "`[days]`") + "\n" +
                    devi.getTranslation(command.getLanguage(), 598, "`[reason]`") + "\n\n" +
                    devi.getTranslation(command.getLanguage(), 594, "`[days]`", "`[reason]`")));
            return;
        }

        User user = DiscordUtils.getUser(args[0], command.getGuild());
        if (user == null) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 13, "`" + args[0] + "`"));
            return;
        }
        Member member = command.getGuild().getMember(user);

        boolean skipDays = false;
        String reason = devi.getTranslation(command.getLanguage(), 595);

        int days = -1;
        try {
            if (args.length > 1)
                days = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            skipDays = true;
        }
        if (days > 7 || days < 0) skipDays = true;

        if (!PermissionUtil.canInteract(command.getMember(), member) || user.getId().equals(sender.getId()) || command.getJDA().getSelfUser().getId().equals(sender.getId())) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 15));
            return;
        }

        if (sender.getId().equalsIgnoreCase(user.getId()) ||
                !PermissionUtil.checkPermission(command.getGuild().getSelfMember(), Permission.BAN_MEMBERS) ||
                !PermissionUtil.canInteract(command.getGuild().getSelfMember(), member)) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 16));
            return;
        }

        if (args.length > 1)
            reason = Arrays.stream(args).skip(skipDays ? 1 : 2).collect(Collectors.joining(" "));

        new PunishmentBuilder(command.getDeviGuild())
                .setReason(reason)
                .setOptions(new BanOptions().setDays(skipDays ? 0 : days))
                .setPunished(member.getUser())
                .setPunisher(sender)
                .setType(Punishment.Type.BAN)
                .build()
                .execute(success -> sender.reply(Emote.SUCCESS + " | " + devi.getTranslation(command.getLanguage(), 596, "`" + user.getName() + "#" + user.getDiscriminator() + "`")),
                        error -> sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 25)));

    }
}
