package net.devibot.provider.commands.management;

import net.devibot.provider.commands.CommandSender;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.core.DiscordBot;
import net.devibot.core.entities.Emote;
import net.devibot.core.entities.Language;
import net.devibot.provider.utils.MessageBuilder;
import net.devibot.provider.utils.Reactions;
import net.devibot.provider.utils.Translator;

public class LanguageCommand extends ICommand {

    private DiscordBot discordBot;

    public LanguageCommand(DiscordBot discordBot) {
        super("language", "lang");
        this.discordBot = discordBot;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        //send them the translation menu with reactions
        sender.message().append(getTranslatedInitialMessage(sender)).execute(message -> {
            Reactions.reactionGUI(command.getJDA(), sender.getId(), Reactions.ReactionAwaiterUserType.USER, message, callback -> {
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
                    msg.editMessage(new MessageBuilder()
                            .setLanguage(command.getLanguage())
                            .append(getTranslatedInitialMessage(sender))
                            .append("\n")
                            .append(Emote.SUCCESS)
                            .append(" | ")
                            .appendTranslation("commands.language.success", Translator.getTranslation(language, language.getTranslationKey()))
                            .build()).queue(placeHolder -> {}, placeHolder -> {});
                });
            }, timeOut -> {
                //get message again because it might have been deleted
                command.getChannel().getMessageById(timeOut).queue(msg -> {
                    //update language so we get the correct translation
                    Language lang = Language.getLanguage(command.getDeviGuild().getLanguage());
                    //update the message and clear reactions
                    msg.editMessage(new MessageBuilder(msg)
                            .setLanguage(lang)
                            .append("\n\n")
                            .append(Emote.INFO)
                            .append(" | ")
                            .appendTranslation("commands.language.error.timeout")
                            .build()).queue(placeHolder -> {}, placeHolder -> {});
                    //clear reactions
                    msg.clearReactions().queue(placeHolder -> {}, placeHolder -> {});
                }, placeHolder -> {});
            }, Language.getAllLanguageFlags());
        });
    }

    private String getTranslatedInitialMessage(CommandSender sender) {
        MessageBuilder msg = sender.infoMessage().append("**").append(sender.getName()).append("**, ").appendTranslation("commands.language.info.select").append("\n\n");

        int totalTranslations = Translator.getTranslations().get(Language.ENGLISH).keySet().size();

        for (Language language : Language.values()) {
            int translated = Translator.getTranslations().get(language).size();
            //noinspection ResultOfMethodCallIgnored
            msg.append("● ").append(language.getEmojiFlag()).append(" ").appendTranslation(language.getTranslationKey()).append(" (").append(Math.round(((double) translated / (double) totalTranslations) * 100))
                    .append("% ").appendTranslation("commands.language.translated").append(")\n");
        }

        return msg.getStringBuilder().toString();
    }
}
