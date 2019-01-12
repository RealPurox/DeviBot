package net.devibot.provider.commands.management;

import net.devibot.core.utils.JavaUtils;
import net.devibot.provider.commands.CommandSender;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.core.DiscordBot;
import net.devibot.provider.entities.Emote;
import net.devibot.provider.utils.Translator;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class AutoModCommand extends ICommand {

    private DiscordBot discordBot;

    public AutoModCommand(DiscordBot discordBot) {
        super("automod");
        this.discordBot = discordBot;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        String[] args = command.getArgs();

        if (args.length == 0) {
            sender.reply(getDefaultEmbed(command));
            return;
        }

        if (args.length == 1) {
            Boolean bool = JavaUtils.getBoolean(args[0]);
            if (bool == null) {
                sender.reply(getDefaultEmbed(command));
                return;
            }
            command.getDeviGuild().setAutoModEnabled(bool);
            discordBot.getMainframeManager().requestDeviGuildSettingsSave(command.getDeviGuild());
            sender.reply(Emote.AUTO_MOD +  " | " + Translator.getTranslation(command.getLanguage(), command.getDeviGuild().isAutoModEnabled() ? 286 : 287));
            return;
        }

        if (args[0].equalsIgnoreCase("ads")) {
            Boolean bool = JavaUtils.getBoolean(args[1]);
            if (bool == null) {
                sender.reply(getDefaultEmbed(command));
                return;
            }
            command.getDeviGuild().setAutoModAntiAds(bool);
            discordBot.getMainframeManager().requestDeviGuildSettingsSave(command.getDeviGuild());
            sender.reply(Emote.AUTO_MOD +  " | " + Translator.getTranslation(command.getLanguage(), command.getDeviGuild().isAutoModAntiAds() ? 280 : 281));
        } else if (args[0].equalsIgnoreCase("caps")) {
            Boolean bool = JavaUtils.getBoolean(args[1]);
            if (bool == null) {
                sender.reply(getDefaultEmbed(command));
                return;
            }
            command.getDeviGuild().setAutoModAntiCaps(bool);
            discordBot.getMainframeManager().requestDeviGuildSettingsSave(command.getDeviGuild());
            sender.reply(Emote.AUTO_MOD +  " | " + Translator.getTranslation(command.getLanguage(), command.getDeviGuild().isAutoModAntiCaps() ? 282 : 283));
        } else if (args[0].equalsIgnoreCase("emoji")) {
            Boolean bool = JavaUtils.getBoolean(args[1]);
            if (bool == null) {
                sender.reply(getDefaultEmbed(command));
                return;
            }
            command.getDeviGuild().setAutoModAntiEmojiSpam(bool);
            discordBot.getMainframeManager().requestDeviGuildSettingsSave(command.getDeviGuild());
            sender.reply(Emote.AUTO_MOD +  " | " + Translator.getTranslation(command.getLanguage(), command.getDeviGuild().isAutoModAntiEmojiSpam() ? 284 : 285));
        } else {
            sender.reply(getDefaultEmbed(command));
        }
    }

    private MessageEmbed getDefaultEmbed(Command command) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(0x7289DA);
        builder.setAuthor(Translator.getTranslation(command.getLanguage(), 74));

        if (command.getDeviGuild().isAutoModEnabled())
            builder.setDescription(Translator.getTranslation(command.getLanguage(), 629,  "`" + command.getDeviGuild().getPrefix() + "automod off`") + " " + Emote.AUTO_MOD);
        else builder.setDescription(Translator.getTranslation(command.getLanguage(), 630,  "`" + command.getDeviGuild().getPrefix() + "automod off`") + " " + Emote.AUTO_MOD);

        //advertising
        builder.addField(Emote.ADVERTISING + " " + Translator.getTranslation(command.getLanguage(), 77),
                Translator.getTranslation(command.getLanguage(), command.getDeviGuild().isAutoModAntiAds() ? 302 : 303) + "\n`" + command.getDeviGuild().getPrefix() + "automod ads <on/off>`\n\n",
                false);
        //anti caps
        builder.addField(Emote.CAPSLOCK + " " + Translator.getTranslation(command.getLanguage(), 81),
                Translator.getTranslation(command.getLanguage(), command.getDeviGuild().isAutoModAntiCaps() ? 302 : 303) + "\n`" + command.getDeviGuild().getPrefix() + "automod caps <on/off>`\n\n",
                false);
        //emoji spam
        builder.addField(Emote.EMOJI + " " + Translator.getTranslation(command.getLanguage(), 161),
                Translator.getTranslation(command.getLanguage(), command.getDeviGuild().isAutoModAntiEmojiSpam() ? 302 : 303) + "\n`" + command.getDeviGuild().getPrefix() + "automod emoji <on/off>`\n\n",
                false);

        return builder.build();
    }
}
