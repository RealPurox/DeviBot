package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.music.GuildPlayer;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.util.Collections;
import java.util.List;

public class JoinCommandExecutor implements CommandExecutor {

    private Devi devi;

    public JoinCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (!command.getEvent().getMember().getVoiceState().inVoiceChannel()) {
            sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 100));
            return;
        }


        Guild guild = command.getEvent().getGuild();
        VoiceChannel channel = command.getEvent().getMember().getVoiceState().getChannel();
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(guild);

        if (!devi.getMusicManager().isDJorAlone(command.getEvent().getMember(), channel)) {
            sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 454));
            return;
        }

        guildPlayer.join(command, sender, false);
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 99;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("summon");
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
