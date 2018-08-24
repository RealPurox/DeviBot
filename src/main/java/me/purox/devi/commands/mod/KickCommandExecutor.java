package me.purox.devi.commands.mod;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.utils.DiscordUtils;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KickCommandExecutor implements CommandExecutor {

    private Devi devi;

    public KickCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if(args.length < 2){
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "kick <user> <reason>`"));
            return;
        }

        User user = DiscordUtils.getUser(args[0], command.getEvent().getGuild());
        if (user == null) {
            sender.reply(devi.getTranslation(command.getLanguage(), 13, "`" + args[0] + "`"));
            return;
        }

        Member member = command.getEvent().getGuild().getMember(user);
        if (!PermissionUtil.canInteract(command.getEvent().getMember(), member) || user.getId().equals(sender.getId()) || command.getEvent().getJDA().getSelfUser().getId().equals(sender.getId())) {
            sender.reply( devi.getTranslation(command.getLanguage(), 528));
            return;
        }

        if (sender.getId().equalsIgnoreCase(user.getId()) ||
                !PermissionUtil.checkPermission(command.getEvent().getGuild().getSelfMember(), Permission.KICK_MEMBERS) ||
                !PermissionUtil.canInteract(command.getEvent().getGuild().getSelfMember(), member)) {
            sender.reply( devi.getTranslation(command.getLanguage(), 529));
            return;
        }

        String reason = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));

        command.getEvent().getGuild().getController().kick(member).queue(
                success -> {
                    devi.getModLogManager().logKick(command.getDeviGuild(), member, command.getEvent().getMember(), reason);
                    MessageUtils.sendPrivateMessageAsync(user, devi.getTranslation(command.getLanguage(), 532, "**" + command.getEvent().getGuild().getName() + "**", "\"" + reason + "\""));
                    sender.reply((Emote.SUCCESS.get() + " " + devi.getTranslation(command.getLanguage(), 535, "**"+user.getName()+"#"+user.getDiscriminator()+"**", "`"+reason+"`")));
                },
                error -> sender.reply(devi.getTranslation(command.getLanguage(), 531))
        );
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 536;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return Permission.KICK_MEMBERS;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.MODERATION;
    }
}

