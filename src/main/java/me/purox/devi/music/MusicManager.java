package me.purox.devi.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MusicManager {

    private Devi devi;
    private AudioPlayerManager audioPlayerManager;
    private HashMap<String, GuildPlayer> guildPlayers;

    public MusicManager (Devi devi) {
        this.devi = devi;
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        this.guildPlayers = new HashMap<>();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);

        //check every 2 min if we can destroy GuildPlayers
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            guildPlayers.values().forEach(guildPlayer -> {
                if (guildPlayer.getDestroyTime() <= System.currentTimeMillis()) {
                    guildPlayer.destroy(true);
                }
            });
        }, 2, 2, TimeUnit.MINUTES);
    }

    public GuildPlayer getGuildPlayer(Guild guild) {
        if (!guildPlayers.containsKey(guild.getId())) {
            devi.getLogger().log("Created GuildPlayer for guild " + guild.getName() + " (" + guild.getId() + ")");
            guildPlayers.put(guild.getId(), new GuildPlayer(devi, guild));
        }
        return guildPlayers.get(guild.getId());
    }

    AudioPlayerManager getAudioPlayerManager() {
        return audioPlayerManager;
    }

    public boolean isDJorAlone(Member member, VoiceChannel voiceChannel, Guild guild) {
        //IDs of members in that voice channel
        Set<String> voiceMembers = voiceChannel == null ? new HashSet<>() : voiceChannel.getMembers().stream().map(mem -> mem.getUser().getId()).collect(Collectors.toSet());
        if (member.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("DJ"))) {
            devi.getLogger().debug(member.getUser().getName() + "#" + member.getUser().getDiscriminator() + " has DJ role in guild " + guild.getName() + " (" + guild.getId() + ")");
            return true;
        } else if (member.hasPermission(Permission.MANAGE_CHANNEL)) {
            devi.getLogger().debug(member.getUser().getName() + "#" + member.getUser().getDiscriminator() + " has Manage Channel permission in guild " + guild.getName() + " (" + guild.getId() + ")");
            return true;
        } else if (voiceMembers.containsAll(Arrays.asList(member.getUser().getId(), guild.getJDA().getSelfUser().getId()))) {
            devi.getLogger().debug(member.getUser().getName() + "#" + member.getUser().getDiscriminator() + " is with just the bot in the channel in guild " + guild.getName() + " (" + guild.getId() + ")");
            return true;
        } else if (voiceMembers.size() == 1 && voiceMembers.contains(member.getUser().getId())) {
            devi.getLogger().debug(member.getUser().getName() + "#" + member.getUser().getDiscriminator() + " is totally alone in the channel in guild " + guild.getName() + " (" + guild.getId() + ")");
            return true;
    }
        devi.getLogger().debug(member.getUser().getName() + "#" + member.getUser().getDiscriminator() + " has not authority to use that command in guild " + guild.getName() + " (" + guild.getId() + ")");
        return false;
    }

    public String getTrackTime(long milliseconds) {
        long totalSecs = milliseconds / 1000;
        long hours = (totalSecs / 3600);
        long mins = (totalSecs / 60) % 60;
        long secs = totalSecs % 60;
        return "(`" + (hours == 0 ? "" : String.format("%02d", hours) + "`:`") + String.format("%02d", mins) + "`:`" + String.format("%02d", secs) + "`)";
    }

    public HashMap<String, GuildPlayer> getGuildPlayers() {
        return guildPlayers;
    }
}
