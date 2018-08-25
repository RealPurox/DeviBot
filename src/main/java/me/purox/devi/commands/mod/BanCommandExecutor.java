package me.purox.devi.commands.mod;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.punishments.Punishment;
import me.purox.devi.punishments.PunishmentBuilder;
import me.purox.devi.punishments.options.BanOptions;
import me.purox.devi.utils.DiscordUtils;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BanCommandExecutor implements CommandExecutor {

    private Devi devi;

    public BanCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length < 1) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "ban <user> [days] [reason]`\n\n" +
                    devi.getTranslation(command.getLanguage(), 594, "`[days]`", "`[reason]`")));
            return;
        }

        User user = DiscordUtils.getUser(args[0], command.getEvent().getGuild());
        if (user == null) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 13, "`" + args[0] + "`"));
            return;
        }
        Member member = command.getEvent().getGuild().getMember(user);

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

        if (!PermissionUtil.canInteract(command.getEvent().getMember(), member) || user.getId().equals(sender.getId()) || command.getEvent().getJDA().getSelfUser().getId().equals(sender.getId())) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 15));
            return;
        }

        if (sender.getId().equalsIgnoreCase(user.getId()) ||
                !PermissionUtil.checkPermission(command.getEvent().getGuild().getSelfMember(), Permission.BAN_MEMBERS) ||
                !PermissionUtil.canInteract(command.getEvent().getGuild().getSelfMember(), member)) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 16));
            return;
        }

        if (args.length > 1)
            reason = Arrays.stream(args).skip(skipDays ? 1 : 2).collect(Collectors.joining(" "));

        String finalReason = reason;
        new PunishmentBuilder(command.getDeviGuild())
                .setReason(reason)
                .setOptions(new BanOptions().setDays(skipDays ? 0 : days))
                .setPunished(member)
                .setPunisher(sender.getMember())
                .setType(Punishment.Type.BAN)
                .build()
                .execute(success -> {
                    sender.reply(Emote.SUCCESS + " | " + devi.getTranslation(command.getLanguage(), 596, "`" + user.getName() + "#" + user.getDiscriminator() + "`"));
                    MessageUtils.sendPrivateMessageAsync(user, Emote.INFO + devi.getTranslation(command.getLanguage(), 17, "`" + command.getEvent().getGuild().getName() + "`", "\"" + finalReason + "\""));
                }, error -> sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 25)));
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 39;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return Permission.BAN_MEMBERS;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.MODERATION;
    }
}
