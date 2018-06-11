package me.purox.devi.commands.dev;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PMOwnersCommandExecutor implements CommandExecutor {

    private Devi devi;

    public PMOwnersCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (!devi.getAdmins().contains(sender.getId()) && !sender.isConsoleCommandSender()) return;

        if (args.length == 0) {
            sender.reply("Please provide a message");
            return;
        }

        List<Guild> guilds = new ArrayList<>();
        devi.getShardManager().getShards().forEach(jda -> guilds.addAll(jda.getGuilds()));

        for (Guild guild : guilds) {
            Member member = guild.getOwner();
            MessageUtils.sendPrivateMessageAsync(member.getUser(), Arrays.stream(args).collect(Collectors.joining(" ")));
        }
    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 0;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return null;
    }
}
