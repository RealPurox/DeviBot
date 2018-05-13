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
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.List;

public class UnbanCommand implements Command {

    private Devi devi;

    public UnbanCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event, CommandSender sender) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        String prefix = deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);

        if (args.length < 1) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 12, "`" + prefix + "unban <user>`"));
            return;
        }

        if (DiscordUtils.getUser(args[0], event.getGuild()) != null) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 19));
            return;
        }

        User bannedUser = getUser(args[0], event.getGuild().getBanList().complete());
        if (bannedUser == null) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 13, "`" + args[0] + "`"));
            return;
        }

        if (!PermissionUtil.checkPermission(event.getGuild().getSelfMember(), Permission.BAN_MEMBERS)) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 21));
            return;
        }

        event.getGuild().getController().unban(bannedUser).queue(
                success -> {
                    MessageUtils.sendMessage(event.getChannel(), ":ok_hand: " + devi.getTranslation(language, 22, bannedUser.getName() + "#" + bannedUser.getDiscriminator()));
                    if (deviGuild.getBanned().containsKey(bannedUser.getId())) {
                        deviGuild.getBanned().remove(bannedUser.getId());
                        deviGuild.saveSettings();
                    }
                }
        );
    }

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
}
