package me.purox.devi.commands.mod;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.punishments.Punishment;
import me.purox.devi.punishments.PunishmentBuilder;
import me.purox.devi.utils.DiscordUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UnbanCommandExecutor implements CommandExecutor {

    private Devi devi;

    public UnbanCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length < 1) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "unban <user> [reason]`\n\n" +
                    devi.getTranslation(command.getLanguage(), 599, "`[reason]`")));
            return;
        }

        if (DiscordUtils.getUser(args[0], command.getEvent().getGuild()) != null) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 19));
            return;
        }

        //todo retrieve ban history async
        //todo perms check
        User bannedUser = getUser(args[0], command.getEvent().getGuild().getBanList().complete());
        if (bannedUser == null) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 13, "`" + args[0] + "`"));
            return;
        }

        if (!PermissionUtil.checkPermission(command.getEvent().getGuild().getSelfMember(), Permission.BAN_MEMBERS)) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 21));
            return;
        }

        String reason = args.length == 1 ? "Unknown reason" : Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        new PunishmentBuilder(command.getDeviGuild())
                .setReason(reason)
                .setType(Punishment.Type.UNBAN)
                .setPunished(bannedUser)
                .setPunisher(sender)
                .build().execute(success -> sender.reply(Emote.SUCCESS + " | " + devi.getTranslation(command.getLanguage(), 600, "`" + bannedUser.getName() + "#" + bannedUser.getDiscriminator() + "`")),
                        error -> sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 601)));
    }

    //TODO return ban entry
    private User getUser(String input, List<Guild.Ban> banList) {
        for (Guild.Ban ban : banList) {
            if (ban.getUser().getAsMention().equals(input)) return ban.getUser();
            if (ban.getUser().getId().equals(input)) return ban.getUser();
            if (ban.getUser().getName().equals(input)) return ban.getUser();
        }
        return null;
    }
    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 43;
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
