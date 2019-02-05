package net.devibot.provider.commands.management;

import net.devibot.provider.commands.CommandSender;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.core.DiscordBot;
import net.devibot.core.entities.Emote;
import net.devibot.core.entities.Language;
import net.devibot.provider.utils.Reactions;
import net.devibot.provider.utils.Translator;
import net.dv8tion.jda.core.MessageBuilder;

public class LanguageCommand extends ICommand {

    private DiscordBot discordBot;

    public LanguageCommand(DiscordBot discordBot) {
        super("language", "lang");
        this.discordBot = discordBot;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        //send them the translation menu with reactions
        sender.reply(getTranslatedInitialMessage(sender, command.getLanguage()), (message) -> Reactions.reactionGUI(command.getJDA(), sender.getId(), Reactions.ReactionAwaiterUserType.USER, message, callback -> {
            //get language by the emote they reacted with
            Language language = Language.getLanguage(callback);
            //language not found (they probably reacted with something else)
            if (language == null) return;

            //update & save settings
            command.getDeviGuild().setLanguage(language.name());
            discordBot.getMainframeManager().saveDeviGuild(command.getDeviGuild());

            //get message again because it might have been deleted
            command.getChannel().getMessageById(message.getId()).queue(msg -> {
                //edit the message
                msg.editMessage(getTranslatedInitialMessage(sender, language) + "\n" + Emote.SUCCESS + " | " + Translator.getTranslationOLD(language, 258, Translator.getTranslationOLD(language, language.getTranslationId()))).queue(placeHolder -> {}, placerHolder -> {});
            }, placeHolder -> {});
        }, timeOut -> {
            //get message again because it might have been deleted
            command.getChannel().getMessageById(timeOut).queue(msg -> {
                //update language so we get the correct translation
                Language lang = Language.getLanguage(command.getDeviGuild().getLanguage());
                //update the message and clear reactions
                msg.editMessage(new MessageBuilder(msg).append("\n\n").append(Emote.INFO).append(" | ").append(Translator.getTranslationOLD(lang, 626)).build()).queue();
                msg.clearReactions().queue(placeHolder -> {}, placeHolder -> {});
            }, placeHolder -> {});
        }, Language.getAllLanguageFlags()));
    }

    private String getTranslatedInitialMessage(CommandSender sender, Language lang) {
        StringBuilder msg = new StringBuilder(Emote.INFO + " | **" + sender.getName() + "**, " + Translator.getTranslationOLD(lang, 625) + "\n\n");

        int totalTranslations = Translator.getTranslationsOLD().get(Language.ENGLISH).keySet().size();

        for (Language language : Language.values()) {
            int translated = Translator.getTranslationsOLD().get(language).size();
            msg.append("● ").append(language.getEmojiFlag()).append(" ").append(Translator.getTranslationOLD(lang, language.getTranslationId())).append(" (").append(Math.round(((double) translated / (double) totalTranslations) * 100))
                    .append("% ").append(Translator.getTranslationOLD(lang, 558)).append(")\n");
        }

        return msg.toString();
    }
}
