package me.purox.devi.commands.music;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.entities.Emote;
import me.purox.devi.music.GuildPlayer;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class VolumeCommand extends ICommand {

    private Devi devi;

    public VolumeCommand(Devi devi) {
        super("volume");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getGuild());
        if (command.getArgs().length == 0) {
            sender.reply(getVolumeEmbed(guildPlayer, command));
            return;
        }

        int volume;

        try {
            volume = Integer.parseInt(command.getArgs()[0]);
        } catch (NumberFormatException e) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 589));
            return;
        }

        if (volume > 150 || volume < 1) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 589));
            return;
        }

        guildPlayer.getAudioPlayer().setVolume(volume);
        sender.reply(Emote.SUCCESS + " | " + devi.getTranslation(command.getLanguage(), 190, volume));

    }

    private MessageEmbed getVolumeEmbed(GuildPlayer guildPlayer, Command command) {
        EmbedBuilder builder = new EmbedBuilder().setTitle(devi.getTranslation(command.getLanguage(), 587));
        int volume = guildPlayer.getAudioPlayer().getVolume() / 10;

        StringBuilder progressBar = new StringBuilder(" ~~**[");
        for (int i = 0; i < volume; i++) {
            progressBar.append("--");
        }
        progressBar.append("](https://www.devibot.net)");
        for (int i = 0; i < 15 - volume; i++) {
            progressBar.append("--");
        }
        progressBar.append("**~~ ");

        builder.setDescription("`" + guildPlayer.getAudioPlayer().getVolume() + "` " + progressBar.toString() + " `150`");
        builder.setFooter(devi.getTranslation(command.getLanguage(), 588, command.getPrefix()+ "volume <1- 150>"), null);
        return builder.build();
    }

}
