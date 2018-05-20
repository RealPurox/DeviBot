package me.purox.devi.commands.mod;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.utils.DiscordUtils;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.bson.Document;

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
        if (args.length < 3) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "ban <user> <days> <reason>`"));
            return;
        }

        User user = DiscordUtils.getUser(args[0], command.getEvent().getGuild());
        if (user == null) {
            sender.reply(devi.getTranslation(command.getLanguage(), 13, "`" + args[0] + "`"));
            return;
        }
        Member member = command.getEvent().getGuild().getMember(user);

        int days;
        try {
            days = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            days = -1;
        }

        if (days > 7 || days < 0) {
            sender.reply( devi.getTranslation(command.getLanguage(), 14));
            return;
        }

        if (!PermissionUtil.canInteract(command.getEvent().getMember(), member) || user.getId().equals(sender.getId()) || command.getEvent().getJDA().getSelfUser().getId().equals(sender.getId())) {
            sender.reply( devi.getTranslation(command.getLanguage(), 15));
            return;
        }

        if (sender.getId().equalsIgnoreCase(user.getId()) ||
                !PermissionUtil.checkPermission(command.getEvent().getGuild().getSelfMember(), Permission.BAN_MEMBERS) ||
                !PermissionUtil.canInteract(command.getEvent().getGuild().getSelfMember(), member)) {
            sender.reply( devi.getTranslation(command.getLanguage(), 16));
            return;
        }

        String reason = Arrays.stream(args).skip(2).collect(Collectors.joining("", "", ""));
        MessageUtils.sendPrivateMessage(user, devi.getTranslation(command.getLanguage(), 17, "**" + command.getEvent().getGuild().getName() + "**", "\"" + reason + "\""));
        int day = days;
        command.getEvent().getGuild().getController().ban(user, days).queue(
                success -> {
                    Document punishment = new Document();
                    punishment.put("user", user.getName() + "#" + user.getDiscriminator());
                    punishment.put("reason", reason);
                    punishment.put("punisher", sender.getName() + "#" + sender.getDiscriminator());

                    command.getDeviGuild().getBanned().put(user.getId(), punishment);
                    command.getDeviGuild().saveSettings();
                    devi.getModLogManager().logBan(command.getDeviGuild(), member, command.getEvent().getMember(), reason);

                    if (day > 0) {
                        sender.reply( ":ok_hand: " + devi.getTranslation(command.getLanguage(), 18, "**"+user.getName()+"#"+user.getDiscriminator()+"**", day));
                    } else {
                        sender.reply( ":ok_hand: " + devi.getTranslation(command.getLanguage(), 67, "**"+user.getName()+"#"+user.getDiscriminator()+"**"));
                    }
                },
                error -> sender.reply( devi.getTranslation(command.getLanguage(), 25))
        );
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
}
