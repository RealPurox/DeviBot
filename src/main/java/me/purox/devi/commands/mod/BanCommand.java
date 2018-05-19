package me.purox.devi.commands.mod;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.Language;
import me.purox.devi.utils.DiscordUtils;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BanCommand implements Command {

    private Devi devi;

    public BanCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event, CommandSender sender) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        String prefix = deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);

        if (args.length < 3) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 12, "`" + prefix + "ban <user> <days> <reason>`"));
            return;
        }

        User user = DiscordUtils.getUser(args[0], event.getGuild());
        if (user == null) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 13, "`" + args[0] + "`"));
            return;
        }
        Member member = event.getGuild().getMember(user);

        int days;
        try {
            days = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            days = -1;
        }

        if (days > 7 || days < 0) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 14));
            return;
        }

        if (!PermissionUtil.canInteract(event.getMember(), member) || user.getId().equals(event.getAuthor().getId()) || event.getJDA().getSelfUser().getId().equals(event.getAuthor().getId())) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 15));
            return;
        }

        if (event.getAuthor().getId().equalsIgnoreCase(user.getId()) ||
                !PermissionUtil.checkPermission(event.getGuild().getSelfMember(), Permission.BAN_MEMBERS) ||
                !PermissionUtil.canInteract(event.getGuild().getSelfMember(), member)) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 16));
            return;
        }

        String reason = Arrays.stream(args).skip(2).collect(Collectors.joining("", "", ""));
        MessageUtils.sendPrivateMessage(user, devi.getTranslation(language, 17, "**" + event.getGuild().getName() + "**", "\"" + reason + "\""));
        int day = days;
        event.getGuild().getController().ban(user, days).queue(
                success -> {
                    Document punishment = new Document();
                    punishment.put("user", user.getName() + "#" + user.getDiscriminator());
                    punishment.put("reason", reason);
                    punishment.put("punisher", event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator());

                    deviGuild.getBanned().put(user.getId(), punishment);
                    deviGuild.saveSettings();
                    devi.getModLogManager().logBan(deviGuild, member, event.getMember(), reason);

                    if (day > 0) {
                        MessageUtils.sendMessage(event.getChannel(), ":ok_hand: " + devi.getTranslation(language, 18, "**"+user.getName()+"#"+user.getDiscriminator()+"**", day));
                    } else {
                        MessageUtils.sendMessage(event.getChannel(), ":ok_hand: " + devi.getTranslation(language, 67, "**"+user.getName()+"#"+user.getDiscriminator()+"**"));
                    }
                },
                error -> MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 25))
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
