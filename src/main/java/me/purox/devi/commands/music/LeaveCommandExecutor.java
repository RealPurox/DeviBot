package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.music.GuildPlayer;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.List;

public class LeaveCommandExecutor implements CommandExecutor {

    private Devi devi;

    public LeaveCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        Guild guild = command.getEvent().getGuild();
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(guild);
        VoiceChannel channel = command.getEvent().getMember().getVoiceState().getChannel();

        if (!devi.getMusicManager().isDJorAlone(command.getEvent().getMember(), channel, guild)) {
            sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 454));
            return;
        }

        guildPlayer.leave(command, sender, false);
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 143;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return null;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.MUSIC;
    }
}
