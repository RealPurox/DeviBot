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
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.List;

public class UnmuteCommand implements Command {

    private Devi devi;
    public UnmuteCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event, CommandSender sender) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        String prefix = deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);

        if (args.length < 1) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 12, "`" + prefix + "unmute <user>`"));
            return;
        }

        User user = DiscordUtils.getUser(args[0], event.getGuild());
        if (user == null) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 13, "`" + args[0] + "`"));
            return;
        }

        if (!deviGuild.getMuted().containsKey(user.getId())){
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 52));
            return;
        }

        if(!PermissionUtil.canInteract(event.getMember(), event.getGuild().getMemberById(user.getId())) || user.getId().equals(event.getJDA().getSelfUser().getId()) || user.getId().equals(event.getAuthor().getId())){
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 53));
            return;
        }

        if(!PermissionUtil.checkPermission(event.getGuild().getSelfMember(), Permission.MANAGE_ROLES) || !PermissionUtil.canInteract(event.getGuild().getSelfMember(), event.getGuild().getMemberById(user.getId()))){
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 54));
            return;
        }

        Role role = event.getGuild().getRoleById(deviGuild.getSettings().getStringValue(GuildSettings.Settings.MUTE_ROLE));
        if (role == null) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 55));
            deviGuild.getMuted().remove(user.getId());
            deviGuild.saveSettings();
            return;
        }

        event.getGuild().getController().removeRolesFromMember(event.getGuild().getMember(user), role).queue(success ->
                MessageUtils.sendMessage(event.getChannel(), ":ok_hand: " + devi.getTranslation(language, 56, user.getName() + "#" + user.getDiscriminator())));
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
