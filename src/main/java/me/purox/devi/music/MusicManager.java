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
import java.util.Set;
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

    public boolean isDJorAlone(Member member, VoiceChannel voiceChannel) {
        //IDs of members in that voice channel
        Set<String> voiceMembers = voiceChannel.getMembers().stream().map(mem -> mem.getUser().getId()).collect(Collectors.toSet());
        if (member.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("DJ"))) {
            devi.getLogger().debug("Member has DJ role");
            return true;
        } else if (member.hasPermission(Permission.MANAGE_CHANNEL)) {
            devi.getLogger().debug("Members has Manage Channel permission");
            return true;
        } else if (voiceMembers.containsAll(Arrays.asList(member.getUser().getId(), voiceChannel.getJDA().getSelfUser().getId()))) {
            devi.getLogger().debug("Member is with just the bot in the channel");
            return true;
        } else if (voiceMembers.size() == 1 && voiceMembers.contains(member.getUser().getId())) {
            devi.getLogger().debug("Member is totally alone in the channel");
            return true;
        }
        devi.getLogger().debug("Member has not authority to use that command");
        return false;
    }

    public HashMap<String, GuildPlayer> getGuildPlayers() {
        return guildPlayers;
    }
}
