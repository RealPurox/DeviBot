package me.purox.devi.commandsold.music;

import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandExecutor;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.music.GuildPlayer;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.util.List;

public class VolumeCommandExecutor implements CommandExecutor {

    private Devi devi;

    public VolumeCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, ICommand command, CommandSender sender) {
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getEvent().getGuild());
        if (args.length == 0) {
            sender.reply(getVolumeEmbed(guildPlayer, command));
            return;
        }

        int volume;

        try {
            volume = Integer.parseInt(args[0]);
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

    private MessageEmbed getVolumeEmbed(GuildPlayer guildPlayer, ICommand command) {
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

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 189;
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
        return null;
    }
}
