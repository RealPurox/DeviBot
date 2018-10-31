package me.purox.devi.commands.music;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.music.GuildPlayer;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class LeaveCommand extends ICommand {

    private Devi devi;

    public LeaveCommand(Devi devi) {
        super("leave");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        Guild guild = command.getGuild();
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(guild);
        VoiceChannel channel = command.getMember().getVoiceState().getChannel();

        if (!devi.getMusicManager().isDJorAlone(command.getMember(), channel, guild)) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 454));
            return;
        }

        guildPlayer.leave(command, sender, false);
    }
}
