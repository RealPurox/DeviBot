package me.purox.devi.commandsold.info;

import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandExecutor;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;

import java.awt.*;
import java.util.List;

public class SupportCommandExecutor implements CommandExecutor {

    private Devi devi;

    public SupportCommandExecutor(Devi devi){
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, ICommand command, CommandSender sender) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.decode("#4775d1"));
        embed.setTitle(" ");
        embed.setDescription(devi.getTranslation(command.getLanguage(), 489));
        MessageUtils.sendMessageAsync(command.getEvent().getChannel(), new MessageBuilder()
                .setContent("https://discord.gg/6Ac4uqZ")
                .setEmbed(embed.build()).build());
    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 490;
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
