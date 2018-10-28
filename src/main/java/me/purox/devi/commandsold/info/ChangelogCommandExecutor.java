package me.purox.devi.commandsold.info;

import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandExecutor;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.awt.*;
import java.util.Arrays;
import java.util.List;


public class ChangelogCommandExecutor implements CommandExecutor {

    private Devi devi;

    public ChangelogCommandExecutor(Devi devi){
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, ICommand command, CommandSender sender) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.decode("#4775d1"));
        embed.setTitle(" ");
        embed.setDescription(devi.getTranslation(command.getLanguage(), 492));
        sender.reply(embed.build());

    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 491;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("changes", "updates", "changelogs");
    }

    @Override
    public Permission getPermission() {
        return null;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.INFO_COMMANDS;
    }
}
