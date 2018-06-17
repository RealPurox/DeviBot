package me.purox.devi.commands.guild;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.Permission;

import java.util.List;

public class WelcomeCommandExecutor implements CommandExecutor {

    private Devi devi;

    public WelcomeCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        String builder = ":information_source: | You're currently editing the Welcome module\n\n" +
                "```python\n" +
                "Reply with one of the options listed below to edit your Welcome module settings\n\n" +
                " '1' => Enable or disable the welcome module (this won't disable auto roles)\n" +
                " '2' => \n" +
                " '3' => " + devi.getTranslation(command.getLanguage(), 357) + "\n" +
                devi.getTranslation(command.getLanguage(), 359, "'" + command.getPrefix() + "streamlist'") +
                "```\n" + devi.getTranslation(command.getLanguage(), 312, "`cancel`");

        sender.reply(builder);
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 376;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.WELCOME;
    }
}
