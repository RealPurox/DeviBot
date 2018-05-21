package me.purox.devi.commands.mod;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.DiscordUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.List;

public class UnmuteCommandExecutor implements CommandExecutor {

    private Devi devi;
    public UnmuteCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length < 1) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "unmute <user>`"));
            return;
        }

        User user = DiscordUtils.getUser(args[0], command.getEvent().getGuild());
        if (user == null) {
            sender.reply(devi.getTranslation(command.getLanguage(), 13, "`" + args[0] + "`"));
            return;
        }

        if (!command.getDeviGuild().getMuted().containsKey(user.getId())){
            sender.reply(devi.getTranslation(command.getLanguage(), 52));
            return;
        }

        if(!PermissionUtil.canInteract(command.getEvent().getMember(), command.getEvent().getGuild().getMemberById(user.getId())) || user.getId().equals(command.getEvent().getJDA().getSelfUser().getId()) || user.getId().equals(sender.getId())){
            sender.reply(devi.getTranslation(command.getLanguage(), 53));
            return;
        }

        if(!PermissionUtil.checkPermission(command.getEvent().getGuild().getSelfMember(), Permission.MANAGE_ROLES) || !PermissionUtil.canInteract(command.getEvent().getGuild().getSelfMember(), command.getEvent().getGuild().getMemberById(user.getId()))){
            sender.reply(devi.getTranslation(command.getLanguage(), 54));
            return;
        }

        Role role = command.getEvent().getGuild().getRoleById(command.getDeviGuild().getSettings().getStringValue(GuildSettings.Settings.MUTE_ROLE));
        if (role == null) {
            sender.reply(devi.getTranslation(command.getLanguage(), 55));
            command.getDeviGuild().getMuted().remove(user.getId());
            command.getDeviGuild().saveSettings();
            return;
        }

        command.getEvent().getGuild().getController().removeRolesFromMember(command.getEvent().getGuild().getMember(user), role).queue(success ->
                sender.reply(":ok_hand: " + devi.getTranslation(command.getLanguage(), 56, user.getName() + "#" + user.getDiscriminator())));
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 44;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return Permission.VOICE_MUTE_OTHERS;
    }
}
