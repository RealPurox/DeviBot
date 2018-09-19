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

import java.util.Arrays;
import java.util.Collections;
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

        if (!command.getEvent().getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 603));
            return;
        }

        command.getEvent().getGuild().getBanList().queue(bans -> {
            Guild.Ban ban = getBan(args[0], bans);

            if (ban == null) {
                sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 13, "`" + args[0] + "`"));
                return;
            }

            String reason = args.length == 1 ? "Unknown reason" : Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
            new PunishmentBuilder(command.getDeviGuild())
                    .setReason(reason)
                    .setType(Punishment.Type.UNBAN)
                    .setPunished(ban.getUser())
                    .setPunisher(sender)
                    .build().execute(success -> sender.reply(Emote.SUCCESS + " | " +
                            devi.getTranslation(command.getLanguage(), 600, "`" + ban.getUser().getName() + "#" + ban.getUser().getDiscriminator() + "`")),
                    error -> sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 601)));
        }, failure -> sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 604)));
    }

    private Guild.Ban getBan(String input, List<Guild.Ban> banList) {
        for (Guild.Ban ban : banList) {
            if (ban.getUser().getAsMention().equals(input)) return ban;
            if (ban.getUser().getId().equals(input)) return ban;
            //try to not ignore case sensitivity first
            if (ban.getUser().getName().equals(input)) return ban;
            if (ban.getUser().getName().equalsIgnoreCase(input)) return ban;
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
        return Collections.singletonList("pardon");
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
