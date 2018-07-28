package me.purox.devi.commands.info;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;

import java.awt.*;
import java.util.List;


public class ChangelogCommandExecutor implements CommandExecutor {

    private Devi devi;

    public ChangelogCommandExecutor(Devi devi){
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.decode("#4775d1"));
        embed.setTitle(" ");
        embed.setDescription(devi.getTranslation(command.getLanguage(), 492));

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
        return null;
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
