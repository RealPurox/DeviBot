package me.purox.devi.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MusicManager {

    private Devi devi;
    private final AudioPlayerManager manager = new DefaultAudioPlayerManager();
    private final Map<Guild, Map.Entry<AudioPlayer, TrackManager>> audioPlayer = new HashMap<>();

    public MusicManager (Devi devi) {
        AudioSourceManagers.registerRemoteSources(manager);
        this.devi = devi;

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (Guild guild : audioPlayer.keySet()) {
                DeviGuild deviGuild = devi.getDeviGuild(guild.getId());
                Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

                AudioPlayer audioPlayer = getAudioPlayers().get(guild).getKey();
                TrackManager trackManager = getAudioPlayers().get(guild).getValue();

                if (trackManager.getQueue().isEmpty() && audioPlayer.getPlayingTrack() == null && guild.getSelfMember().getVoiceState().inVoiceChannel() &&
                        guild.getSelfMember().getVoiceState().getChannel().getMembers().size() == 1) {
                    EmbedBuilder builder = new EmbedBuilder()
                            .setColor(new Color(34, 113, 126))
                            .setAuthor(devi.getTranslation(language, 85), null, "https://i.pinimg.com/736x/9d/83/17/9d8317162494a004969b79c85d88b5c1--music-logo-dj-music.jpg")
                            .setDescription("**" + devi.getTranslation(language, 247) + "**");
                    trackManager.sendMusicLog(guild, builder.build());
                    trackManager.leaveChannel(guild);
                }
            }
        }, 5, 5, TimeUnit.MINUTES);
    }

    private AudioPlayer createPlayer(Guild guild) {
        AudioPlayer player = manager.createPlayer();
        TrackManager trackManager = new TrackManager(devi, player);

        player.addListener(trackManager);
        player.setVolume(35);

        guild.getAudioManager().setSendingHandler(new PlayerSendHandler(player));
        audioPlayer.put(guild, new AbstractMap.SimpleEntry<>(player, trackManager));

        return player;
    }

    private boolean hasPlayer(Guild guild) {
        return audioPlayer.containsKey(guild);
    }

    public AudioPlayer getPlayer(Guild guild) {
        if (hasPlayer(guild)) return audioPlayer.get(guild).getKey();
        else return createPlayer(guild);
    }

    public TrackManager getManager(Guild guild) {
        if (!audioPlayer.containsKey(guild)) createPlayer(guild);
        return audioPlayer.get(guild).getValue();
    }

    public boolean isIdle(Guild guild) {
        return !hasPlayer(guild) || getPlayer(guild).getPlayingTrack() == null;
    }

    public void loadTrack(MessageReceivedEvent event, String identifier, VoiceChannel channel) {
        Guild guild = channel.getGuild();
        DeviGuild deviGuild = devi.getDeviGuild(guild.getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

        getPlayer(guild);

        manager.setFrameBufferDuration(5000);
        manager.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                getManager(guild).addToQueue(audioTrack, channel);
                MessageUtils.sendMessageAsync(event.getChannel(), new EmbedBuilder()
                        .setColor(new Color(34, 113, 126))
                        .setAuthor(devi.getTranslation(language, 85), null, "https://i.pinimg.com/736x/9d/83/17/9d8317162494a004969b79c85d88b5c1--music-logo-dj-music.jpg")
                        .setDescription("**" + devi.getTranslation(language, 92) + "**")
                        .addField(devi.getTranslation(language, 87), audioTrack.getInfo().title, false)
                        .addField(devi.getTranslation(language, 88), audioTrack.getInfo().author, false)
                        .addField(devi.getTranslation(language, 89), getTimestamp(audioTrack.getInfo().length, language), false)
                        .build());

            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                if (audioPlaylist.isSearchResult()){
                    trackLoaded(audioPlaylist.getTracks().get(0));
                } else {
                    long length = 0;
                    for (AudioTrack track : audioPlaylist.getTracks()) {
                        getManager(guild).addToQueue(track, channel);
                        length += track.getDuration();
                    }
                    MessageUtils.sendMessageAsync(event.getChannel(), new EmbedBuilder()
                            .setColor(new Color(34, 113, 126))
                            .setAuthor(devi.getTranslation(language, 85), null, "https://i.pinimg.com/736x/9d/83/17/9d8317162494a004969b79c85d88b5c1--music-logo-dj-music.jpg")
                            .setDescription("**" + devi.getTranslation(language, 93) + "**")
                            .addField(devi.getTranslation(language, 94), audioPlaylist.getName(), false)
                            .addField(devi.getTranslation(language, 95), String.valueOf(audioPlaylist.getTracks().size()), false)
                            .addField(devi.getTranslation(language, 89), getTimestamp(length, language), false)
                            .build());
                }
            }

            @Override
            public void noMatches() {
                MessageUtils.sendMessageAsync(event.getChannel(), devi.getTranslation(language, 96, identifier));
            }

            @Override
            public void loadFailed(FriendlyException e) {
                MessageUtils.sendMessageAsync(event.getChannel(), devi.getTranslation(language, 97, identifier) + "\n(" + e.getMessage() + ")");
            }
        });
    }

    public void skip(Guild g) {
        getPlayer(g).stopTrack();
    }

    public String getTimestamp(long milliseconds, Language language) {
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000*60)) % 60);
        int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
        return  (hours == 0 ? "" : hours + " " + devi.getTranslation(language, 158) + ", ") +
                (minutes == 0 ? "" :minutes + " " + devi.getTranslation(language, 159) + ", ") +
                (seconds + " " + devi.getTranslation(language, 160));
    }


    public String buildQueueMessage(AudioInfo info, Language language) {
        AudioTrackInfo trackInfo = info.getTrack().getInfo();
        String message = "**" + trackInfo.title + "**\n";
        message += " - " + devi.getTranslation(language, 98) + ": " + getTimestamp(trackInfo.length, language) + "\n";
        message += " - " + devi.getTranslation(language, 88) + ": " + trackInfo.author + "\n";
        return message + "\n";
    }

    public Map<Guild, Map.Entry<AudioPlayer, TrackManager>> getAudioPlayers() {
        return audioPlayer;
    }
}
