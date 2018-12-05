package me.purox.devi.commands.management;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.entities.Emote;
import me.purox.devi.entities.Language;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.waiter.WaitingResponseBuilder;
import me.purox.devi.utils.Reactions;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class LanguageCommand extends ICommand {

    private Devi devi;

    public LanguageCommand(Devi devi) {
        super("language", "lang");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        //send them the translation menu with reactions
        sender.reply(getTranslatedMessage(sender, command.getLanguage()), (message -> Reactions.reactionGUI(sender, message, callback -> {
            //get language by the emote they reacted with
            Language language = Language.getLanguage(callback);
            //language not found (they probably reacted with something else)
            if (language == null) return;

            //update & save settings
            command.getDeviGuild().getSettings().setStringValue(GuildSettings.Settings.LANGUAGE, language.name());
            command.getDeviGuild().saveSettings();

            //get message again because it might have been deleted
            command.getChannel().getMessageById(message.getId()).queue(msg -> {
                //edit the message
                msg.editMessage(getTranslatedMessage(sender, language) + "\n" + Emote.SUCCESS + " | " + devi.getTranslation(language, 258, devi.getTranslation(language, language.getTranslationId()))).queue(placeHolder -> {}, placerHolder -> {});
            }, placeHolder -> {});
        }, timeOut -> {
            //get message again because it might have been deleted
            command.getChannel().getMessageById(timeOut).queue(msg -> {
                //update language so we get the correct translation
                Language lang = Language.getLanguage(command.getDeviGuild().getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
                //update the message and clear reactions
                msg.editMessage(new MessageBuilder(msg).append("\n\n").append(Emote.INFO).append(" | ").append(devi.getTranslation(lang, 626)).build()).queue();
                msg.clearReactions().queue(placeHolder -> {}, placeHolder -> {});
            }, placeHolder -> {});
        }, Language.getAllLanguageFlags())));
    }

    private String getTranslatedMessage(CommandSender sender, Language lang) {
        StringBuilder msg = new StringBuilder(Emote.INFO + " | **" + sender.getName() + "**, " + devi.getTranslation(lang, 625) + "\n\n");

        int totalTrans = devi.getDeviTranslations().get(Language.ENGLISH).keySet().size();

        for (Language language : Language.values()) {
            int translated = devi.getDeviTranslations().get(language).keySet().size();
            msg.append("● ").append(language.getFlag()).append(" ").append(devi.getTranslation(lang, language.getTranslationId())).append(" (").append(Math.round(((double) translated / (double) totalTrans) * 100))
                    .append("% ").append(devi.getTranslation(lang, 558)).append(")\n");
        }

        return msg.toString();
    }
}
