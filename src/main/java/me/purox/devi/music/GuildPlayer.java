package me.purox.devi.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import net.dv8tion.jda.core.audio.hooks.ConnectionListener;
import net.dv8tion.jda.core.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.GuildVoiceState;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.exceptions.GuildUnavailableException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GuildPlayer extends AudioEventAdapter {

    private Devi devi;
    private Guild guild;
    private AudioPlayer audioPlayer;
    private LinkedList<AudioInfo> queue;
    private long destroyTime;

    GuildPlayer(Devi devi, Guild guild) {
        this.devi = devi;
        this.guild = guild;
        this.audioPlayer = devi.getMusicManager().getAudioPlayerManager().createPlayer();
        this.queue = new LinkedList<>();
        this.destroyTime = System.currentTimeMillis() + 300000; //5 mins

        this.audioPlayer.addListener(this);
        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(audioPlayer));
    }

    public String getQueueDuration() {
        long dura = 0;
        for (AudioInfo audioInfo : queue) {
            dura += audioInfo.getAudioTrack().getInfo().length;
        }
        return devi.getMusicManager().getTrackTime(dura);
    }

    public AudioInfo getAudioInfoById(int id) {
        return queue.get(id - 1);
    }

    public int getAudioInfoId(AudioInfo audioInfo) {
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).isEqualTo(audioInfo)) {
                return i + 1;
            }
        }
        return -1;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    private void addToQueue(AudioTrack audioTrack, User requester) {
        AudioInfo audioInfo = new AudioInfo(audioTrack, requester);
        queue.add(audioInfo);

        if (queue.size() == 1) playNext();
    }

    public AudioInfo getAudioInfo(AudioTrack track) {
        return this.queue.stream().filter(audioInfo -> audioInfo.getAudioTrack().equals(track)).findFirst().orElse(null);
    }

    public LinkedList<AudioInfo> getQueue() {
        return queue;
    }

    public List<AudioInfo> getNextSongs(int amount) {
        List<AudioInfo> songs = new ArrayList<>();

        for (int i = 1; i < amount + 1; i++) {
            if (i >= queue.size()) break;

            AudioInfo audioInfo = queue.get(i);

            boolean isInSongsAlready = false;
            for (AudioInfo a : songs) if (a.isEqualTo(audioInfo)) isInSongsAlready = true;

            if(!isInSongsAlready) songs.add(audioInfo);
        }

        return songs;
    }

    public AudioInfo getCurrent() {
        if (audioPlayer.isPaused() || audioPlayer.getPlayingTrack() == null) return null;
        return queue.get(0);
    }

    public void shuffle() {
        AudioInfo currentTrack = this.queue.get(0);

        queue.remove(0);
        Collections.shuffle(queue);
        queue.add(0, currentTrack);
    }

    public void destroy(boolean leave) {
        if (leave) leave(null, null, true);
        audioPlayer.destroy();
        queue.clear();
        devi.getMusicManager().getGuildPlayers().remove(guild.getId());
    }

    private void playNext() {
        if (queue.size() == 0) {
            leave(null, null, true);
            return;
        }

        AudioTrack audioTrack = queue.get(0).getAudioTrack();
        audioPlayer.playTrack(audioTrack);
    }

    long getDestroyTime() {
        return destroyTime;
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        devi.increaseSongsPlayed();
        this.destroyTime = System.currentTimeMillis() + track.getInfo().length + 300000;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason == AudioTrackEndReason.STOPPED) {
            if (queue.size() == 0) {
                leave(null, null, true);
                return;
            }
        }
        queue.remove();
        playNext();
    }

    public void loadSong(String query, Command command, CommandSender sender) {
        devi.getMusicManager().getAudioPlayerManager().loadItem(query, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                if (!command.getEvent().getGuild().getSelfMember().getVoiceState().inVoiceChannel())
                    join(command, sender, true);
                addToQueue(audioTrack, sender);
                sender.reply(DeviEmote.SUCCESS.get() + " | " + devi.getTranslation(command.getLanguage(), 457, "`" + audioTrack.getInfo().title + "`", "__" + audioTrack.getInfo().author + "__") + " "
                        + devi.getMusicManager().getTrackTime(audioTrack.getInfo().length));
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                if (audioPlaylist.isSearchResult()) {
                    trackLoaded(audioPlaylist.getTracks().get(0));
                } else {
                    if (!command.getEvent().getGuild().getSelfMember().getVoiceState().inVoiceChannel())
                        join(command, sender, true);
                    long time = 0;
                    for (int i = 0; i < audioPlaylist.getTracks().size(); i++) {
                        time += audioPlaylist.getTracks().get(i).getInfo().length;
                        addToQueue(audioPlaylist.getTracks().get(i), sender);
                    }
                    sender.reply(DeviEmote.SUCCESS.get() + " | " + devi.getTranslation(command.getLanguage(),458, "`" + audioPlaylist.getName() + "`", "__" + audioPlaylist.getTracks().size() + "__") + " "
                            + devi.getMusicManager().getTrackTime(time));
                }
            }

            @Override
            public void noMatches() {
                sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 459, "`" + (query.startsWith("ytsearch:") ? query.substring(9) : query) + "`"));
            }

            @Override
            public void loadFailed(FriendlyException e) {
                sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 459, "`" + (query.startsWith("ytsearch:") ? query.substring(9) : query) + "`"));
            }
        });
    }

    public void join(Command command, CommandSender sender, boolean silent) {
        AudioManager audioManager = guild.getAudioManager();

        VoiceChannel channel = command.getEvent().getMember().getVoiceState().getChannel();
        GuildVoiceState deviVoiceState = guild.getSelfMember().getVoiceState();

        if (command.getEvent().getGuild().getSelfMember().getVoiceState().inVoiceChannel()){
            if (channel.getIdLong() == deviVoiceState.getChannel().getIdLong()) {
                if(!silent) sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 452));
                return;
            }
            if (audioPlayer.getPlayingTrack() != null) {
                if(!silent) sender.reply(DeviEmote.ERROR.get() + " | " + 453);
                return;
            }
            audioManager.closeAudioConnection();
        }

        ConnectionListener listener = new ConnectionListener() {
            @Override
            public void onPing(long l) { }

            @Override
            public void onStatusChange(ConnectionStatus connectionStatus) {
                //joined voice channel
                if (connectionStatus == ConnectionStatus.CONNECTED) {
                    devi.getLogger().log("Joined voice channel " + channel.getName() + " (" + channel.getId() + ") in guild " + guild.getName() + " (" + guild.getId() + ")");
                    if(!silent) sender.reply(DeviEmote.SUCCESS.get() + " | " + devi.getTranslation(command.getLanguage(), 451));
                    //don't need the listener anymore
                    audioManager.setConnectionListener(null);
                    //lost perms while trying to connect
                } else if (connectionStatus == ConnectionStatus.DISCONNECTED_LOST_PERMISSION) {
                    devi.getLogger().log("Lost permissions while joining voice channel " + channel.getName() + " (" + channel.getId() + ") in guild " + guild.getName() + " (" + guild.getId() + ")");
                    if(!silent) sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 102));
                    destroy(false);
                    //an error occurred
                } else if ((connectionStatus.name().startsWith("ERROR") || connectionStatus.name().startsWith("DISCONNECTED")) && !connectionStatus.name().equals("DISCONNECTED_LOST_PERMISSION")) {
                    devi.getLogger().log("An error occurred while joining voice channel " + channel.getName() + " (" + channel.getId() + ") in guild " + guild.getName() + " (" + guild.getId() + ")");
                    if(!silent) sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 111));
                    destroy(false);
                }
            }

            @Override
            public void onUserSpeaking(User user, boolean b) { }
        };

        audioManager.setAutoReconnect(true);
        audioManager.setConnectionListener(listener);

        try {
            //start connecting
            audioManager.openAudioConnection(channel);
        } catch (IllegalArgumentException e) {
            //member not in a voice channel
            devi.getLogger().log("Failed to join voice channel " + channel.getName() + " (" + channel.getId() + ") in guild " + guild.getName() + " (" + guild.getId() + ")");
            if(!silent) sender.reply(devi.getTranslation(command.getLanguage(), 100));
        } catch (UnsupportedOperationException e) {
            //Audio disabled due to internal error
            devi.getLogger().log("Failed to join voice channel " + channel.getName() + " (" + channel.getId() + ") in guild " + guild.getName() + " (" + guild.getId() + ") (UnsupportedOperationException)");
            if(!silent) sender.reply(devi.getTranslation(command.getLanguage(), 103));
            destroy(true);
        } catch (GuildUnavailableException e) {
            //Guild not available
            devi.getLogger().log("Failed to join voice channel " + channel.getName() + " (" + channel.getId() + ") in guild " + guild.getName() + " (" + guild.getId() + ") (GuildUnavailableException)");
            if(!silent) sender.reply(devi.getTranslation(command.getLanguage(), 104));
            destroy(true);
        } catch (InsufficientPermissionException e) {
            //Insufficient permission to join that channel
            if(!silent) sender.reply(devi.getTranslation(command.getLanguage(), 102));
            destroy(true);
        }
    }

    public void leave(Command command, CommandSender sender, boolean silent) {
        GuildVoiceState deviVoiceState = guild.getSelfMember().getVoiceState();
        AudioManager audioManager = guild.getAudioManager();
        VoiceChannel channel = deviVoiceState.getChannel();

        if (!deviVoiceState.inVoiceChannel()) {
            if(!silent) sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 113));
            return;
        }

        ConnectionListener listener = new ConnectionListener() {
            @Override
            public void onPing(long l) { }

            @Override
            public void onStatusChange(ConnectionStatus connectionStatus) {
                destroy(false);
                audioManager.setConnectionListener(null);
                if(!silent) sender.reply(DeviEmote.SUCCESS.get() + " | " + devi.getTranslation(command.getLanguage(), 455));
                devi.getLogger().log("Left voice channel " + channel.getName() + " (" + channel.getId() + ") in guild " + guild.getName() + " (" + guild.getId() + ")");
            }

            @Override
            public void onUserSpeaking(User user, boolean b) { }
        };

        audioManager.setAutoReconnect(false);
        audioManager.setConnectionListener(listener);
        audioManager.closeAudioConnection();
    }
}
