package me.purox.devi.commands.music;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.entities.Emote;
import me.purox.devi.music.GuildPlayer;
import net.dv8tion.jda.core.entities.Member;

public class ShuffleCommand extends ICommand {

    private Devi devi;

    public ShuffleCommand(Devi devi) {
        super("shuffle");
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getGuild());

        if (guildPlayer.getQueue().isEmpty()) {
            sender.reply(Emote.ERROR + devi.getTranslation(command.getLanguage(), 139));
            return;
        }

        Member member = command.getMember();
        if (!devi.getMusicManager().isDJorAlone(member, member.getVoiceState().getChannel(), member.getGuild())) {
            sender.reply(devi.getTranslation(command.getLanguage(), 454));
            return;
        }

        guildPlayer.shuffle();
        sender.reply(Emote.SUCCESS + " | " + devi.getTranslation(command.getLanguage(), 140));
    }

}
