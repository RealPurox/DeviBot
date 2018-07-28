package me.purox.devi.commands.dev;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import java.util.ArrayList;
import java.util.List;

public class PMOwnersCommandExecutor implements CommandExecutor {

    private Devi devi;

    public PMOwnersCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (!devi.getAdmins().contains(sender.getId())) return;

        if (args.length == 0) {
            sender.reply("Please provide a message");
            return;
        }

        //https://discord.gg/WD59dZ4

        List<Guild> guilds = new ArrayList<>();
        devi.getShardManager().getShards().forEach(jda -> guilds.addAll(jda.getGuilds()));

        String message = command.getEvent().getMessage().getContentRaw().substring(devi.getSettings().getDefaultPrefix().length() + 8);

        for (Guild guild : guilds) {
            Member member = guild.getOwner();
            sender.reply("Sending PM to " + member.getUser().getName() + "#" + member.getUser().getDiscriminator() + " ...");
            MessageUtils.sendPrivateMessageAsync(member.getUser(), message);
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

    @Override
    public ModuleType getModuleType() {
        return ModuleType.DEV;
    }
}

