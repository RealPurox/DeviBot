package me.purox.devi.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.music.AudioInfo;
import me.purox.devi.music.GuildPlayer;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class QueueCommandExecutor implements CommandExecutor {

    private Devi devi;

    public QueueCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getEvent().getGuild());

        if (Arrays.asList(args).contains("--raw") && devi.getAdmins().contains(sender.getId())) {
            StringBuilder sb = new StringBuilder();
            for (AudioInfo audioInfo : guildPlayer.getQueue()) {
                sb.append(audioInfo.toString()).append("\n");
            }
            new RequestBuilder(devi.getOkHttpClient()).setURL("https://hastebin.com/documents").setRequestType(Request.RequestType.POST)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .setStringBody(sb.toString())
                    .build()
                    .asJSON(res -> sender.reply("Raw Queue: https://hastebin.com/" + res.getBody().getString("key")));
            return;
        }

        EmbedBuilder builder = new EmbedBuilder().setColor(Color.decode("#36393E"));

        builder.appendDescription(Emote.MUSIC.get() + " __**" + devi.getTranslation(command.getLanguage(), 463) + "**__ " + Emote.MUSIC.get() + "\n\n");
        //devi.getTranslation(command.getLanguage(), 463)
        boolean displayNext = false;
        if (guildPlayer.getAudioPlayer().isPaused()) {
            builder.appendDescription(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 464) + "\n\n");
        } else if (guildPlayer.getAudioPlayer().getPlayingTrack() == null) {
            builder.appendDescription(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 465) + "\n\n");
        } else {
            if (guildPlayer.getQueue().size() > 1) displayNext = true;
            AudioInfo currentInfo = guildPlayer.getCurrent();
            AudioTrack current = currentInfo.getAudioTrack();

            builder.appendDescription(songIdToString(guildPlayer.getAudioInfoId(currentInfo)) + " " + "[" + current.getInfo().title +"](" + current.getInfo().uri + ") -  " +
                    devi.getTranslation(command.getLanguage(), 466) + " **"
                    + currentInfo.getRequester().getName() + "#" + currentInfo.getRequester().getDiscriminator() + "**\n\n");
        }

        if (displayNext) {
            builder.appendDescription(":arrow_double_down: __**" + devi.getTranslation(command.getLanguage(), 467) + "**__ :arrow_double_down:\n\n");

            int amount = 5;
            List<AudioInfo> audioInfos = guildPlayer.getNextSongs(amount);
            //boolean isMore = audioInfos.size() >= amount;

            for (AudioInfo audioInfo : audioInfos) {
                AudioTrack current = audioInfo.getAudioTrack();
                builder.appendDescription(songIdToString(guildPlayer.getAudioInfoId(audioInfo)) + " " + "[" + current.getInfo().title +"](" + current.getInfo().uri + ") -  " +
                        devi.getTranslation(command.getLanguage(), 466) + " **"
                        + audioInfo.getRequester().getName() + "#" + audioInfo.getRequester().getDiscriminator() + "**\n\n");
            }

            /*if (isMore) {
                builder.appendDescription("[Click here](https://www.devibot.net/guild/" + command.getEvent().getGuild().getId() + "/queue) to display the rest of the queue");
            }*/
        }

        builder.setFooter(devi.getTranslation(command.getLanguage(), 468) + ": " + guildPlayer.getQueue().size() + " " + guildPlayer.getQueueDuration().replaceAll("`", ""), null);
        sender.reply(builder.build());
    }

    private String songIdToString(int id) {
        return String.valueOf(id)
                .replaceAll("0", ":zero:")
                .replaceAll("1", ":one:")
                .replaceAll("2", ":two:")
                .replaceAll("3", ":three:")
                .replaceAll("4", ":four:")
                .replaceAll("5", ":five:")
                .replaceAll("6", ":six:")
                .replaceAll("7", ":seven:")
                .replaceAll("8", ":eight:")
                .replaceAll("9", ":nine:");
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 117;
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
