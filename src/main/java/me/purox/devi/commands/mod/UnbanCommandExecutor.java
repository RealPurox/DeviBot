package me.purox.devi.commands.mod;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.utils.DiscordUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.List;

public class UnbanCommandExecutor implements CommandExecutor {

    private Devi devi;

    public UnbanCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length < 1) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "unban <user>`"));
            return;
        }

        if (DiscordUtils.getUser(args[0], command.getEvent().getGuild()) != null) {
            sender.reply(devi.getTranslation(command.getLanguage(), 19));
            return;
        }

        User bannedUser = getUser(args[0], command.getEvent().getGuild().getBanList().complete());
        if (bannedUser == null) {
            sender.reply(devi.getTranslation(command.getLanguage(), 13, "`" + args[0] + "`"));
            return;
        }

        if (!PermissionUtil.checkPermission(command.getEvent().getGuild().getSelfMember(), Permission.BAN_MEMBERS)) {
            sender.reply(devi.getTranslation(command.getLanguage(), 21));
            return;
        }

        command.getEvent().getGuild().getController().unban(bannedUser).queue(
                success -> {
                    sender.reply(Emote.SUCCESS.get() + " " + devi.getTranslation(command.getLanguage(), 22, bannedUser.getName() + "#" + bannedUser.getDiscriminator()));
                    if (command.getDeviGuild().getBanned().containsKey(bannedUser.getId())) {
                        command.getDeviGuild().getBanned().remove(bannedUser.getId());
                        command.getDeviGuild().saveSettings();
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

    @Override
    public ModuleType getModuleType() {
        return ModuleType.MODERATION;
    }
}
