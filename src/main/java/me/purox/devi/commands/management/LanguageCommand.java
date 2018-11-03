package me.purox.devi.commands.management;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.waiter.WaitingResponseBuilder;

import java.util.Arrays;
import java.util.HashMap;

public class LanguageCommand extends ICommand {

    private Devi devi;
    private HashMap<Integer, Language> languages;

    public LanguageCommand(Devi devi) {
        super("language");
        this.devi = devi;
        this.languages = new HashMap<>();

        for (int i = 1; i < Language.values().length + 1; i++) {
            languages.put(i, Language.values()[i - 1]);
        }
    }

    @Override
    public void execute(CommandSender sender, Command command) {
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
}
