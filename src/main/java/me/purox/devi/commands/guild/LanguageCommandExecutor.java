package me.purox.devi.commands.guild;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.waiter.WaitingResponseBuilder;
import net.dv8tion.jda.core.Permission;

import java.util.HashMap;
import java.util.List;

public class LanguageCommandExecutor implements CommandExecutor {

    private Devi devi;
    private HashMap<Integer, Language> languageMap;

    public LanguageCommandExecutor(Devi devi) {
        this.devi = devi;
        this.languageMap = new HashMap<>();

        int i = 1;
        for (Language language : Language.values()) {
            languageMap.put(i++, language);
        }
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        WaitingResponseBuilder builder = new WaitingResponseBuilder(devi, command)
                .setWaiterType(WaitingResponseBuilder.WaiterType.LANGUAGE)
                .setSetting(GuildSettings.Settings.LANGUAGE);

        StringBuilder inputBuilder = new StringBuilder(devi.getTranslation(command.getLanguage(), 260) + "\n" + devi.getTranslation(command.getLanguage(), 557, "www.devibot.net/translations") + " \n\n");

        int totalTrans = devi.getDeviTranslations().get(Language.ENGLISH).keySet().size();
        for (Language language : Language.values()) {
            int translated = devi.getDeviTranslations().get(language).keySet().size();
            inputBuilder.append(" > ").append(language.getName()).append(" (").append(Math.round(((double) translated / (double) totalTrans) * 100)).append("% ").append(devi.getTranslation(command.getLanguage(), 558)).append(")\n");
        }

        builder.setExpectedInputText(inputBuilder.toString());

        builder.build().handle();
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 253;
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
        return null;
    }
}
