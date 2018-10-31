package me.purox.devi.commands.music;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.music.GuildPlayer;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class JoinCommand extends ICommand {

    private Devi devi;

    public JoinCommand(Devi devi) {
        super("join", "summon");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (!command.getMember().getVoiceState().inVoiceChannel()) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 100));
            return;
        }


        Guild guild = command.getGuild();
        VoiceChannel channel = command.getMember().getVoiceState().getChannel();
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(guild);

        if (!devi.getMusicManager().isDJorAlone(command.getMember(), channel, guild)) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 454));
            return;
        }

        guildPlayer.join(command, sender, false);
    }
}
