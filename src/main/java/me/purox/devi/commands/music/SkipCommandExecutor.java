package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkipCommandExecutor implements CommandExecutor {


    private Devi devi;
    private HashMap<Guild, Map.Entry<Integer, Integer>> guildVotes = new HashMap<>();

    public SkipCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (devi.getMusicManager().isIdle(command.getEvent().getGuild()) || devi.getMusicManager().getPlayer(command.getEvent().getGuild()).isPaused()) {
            sender.reply(devi.getTranslation(command.getLanguage(), 129));
            return;
        }

        if (!command.getEvent().getMember().getVoiceState().inVoiceChannel()) {
            sender.reply(devi.getTranslation(command.getLanguage(), 100));
            return;
        }

        int amount;
        try {
            amount = args.length == 0 ? 1 : Integer.parseInt(args[0]);
        } catch (NumberFormatException e){
            amount = -1;
        }

        if (amount > 10) {
            sender.reply(devi.getTranslation(command.getLanguage(), 133));
            return;
        }

        String oldTrack = devi.getMusicManager().getPlayer(command.getEvent().getGuild()).getPlayingTrack().getInfo().title;

        for (int i = 0; i < amount; i++) {
            devi.getMusicManager().skip(command.getEvent().getGuild());
        }

        String newTrack = devi.getMusicManager().getPlayer(command.getEvent().getGuild()).getPlayingTrack() != null ? devi.getMusicManager().getPlayer(command.getEvent().getGuild()).getPlayingTrack().getInfo().title : "QUEUE_END";

        EmbedBuilder builder = new EmbedBuilder().setColor(new Color(34, 113, 126));
        builder.setAuthor(devi.getTranslation(command.getLanguage(), 85), null, "https://i.pinimg.com/736x/9d/83/17/9d8317162494a004969b79c85d88b5c1--music-logo-dj-music.jpg");

        if (amount == 1) builder.setDescription(":white_check_mark:" + devi.getTranslation(command.getLanguage(), 134, "`" + oldTrack + "`"));
        else builder.setDescription(":white_check_mark:" + devi.getTranslation(command.getLanguage(), 135, amount));

        if (newTrack.equals("QUEUE_END")) builder.appendDescription("\n:track_next: " + devi.getTranslation(command.getLanguage(), 136));
        else builder.appendDescription("\n:track_next: " + devi.getTranslation(command.getLanguage(), 137, "`" + newTrack + "`"));

        sender.reply(builder.build());
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 131;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }
}
