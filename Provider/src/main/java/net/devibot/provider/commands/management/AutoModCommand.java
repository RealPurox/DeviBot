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

    }

    /*@Override
    public void execute(CommandSender sender, Command command) {
        String[] args = command.getArgs();

        if (args.length == 0) {
            sender.reply(getDefaultEmbed(command));
            return;
        }

        Boolean bool = JavaUtils.getBoolean(args[0]);
        //its a boolean so enable / disable auto-mod
        if (bool != null) {
            command.getDeviGuild().setAutoModEnabled(bool);
            discordBot.getMainframeManager().requestDeviGuildSettingsSave(command.getDeviGuild());
            sender.reply(Emote.AUTO_MOD +  " | " + Translator.getTranslation(command.getLanguage(), command.getDeviGuild().isAutoModEnabled() ? 286 : 287));
            return;
        }

        if (args[0].equalsIgnoreCase("invites")) {

            EmbedBuilder builder = new EmbedBuilder()
                    .setColor(0x7289DA)
                    .setAuthor(Translator.getTranslation(command.getLanguage(), 631));

            builder.setDescription("This Auto-Mod module will delete discord invite links and give the sender a strike, if strikes are enabled for this module.");
            builder.addField("Status", command.getDeviGuild().isAutoModAntiInvites() ? Emote.SUCCESS + " | This module is currently enabled" : Emote.ERROR + " | This module is currently disabled" , false);

            sender.reply(builder.build());


        } else {
            sender.reply(getDefaultEmbed(command));
        }
    }

    private MessageEmbed getDefaultEmbed(Command command) {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(0x7289DA)
                .setAuthor(Translator.getTranslation(command.getLanguage(), 74));

        if (command.getDeviGuild().isAutoModEnabled())
            builder.setDescription(Translator.getTranslation(command.getLanguage(), 629,  "`" + command.getDeviGuild().getPrefix() + "automod off`") + " " + Emote.AUTO_MOD);
        else builder.setDescription(Translator.getTranslation(command.getLanguage(), 630,  "`" + command.getDeviGuild().getPrefix() + "automod on`") + " " + Emote.AUTO_MOD);

        //invite links
        builder.addField(Emote.INVITE + " " + Translator.getTranslation(command.getLanguage(), 631),
                Translator.getTranslation(command.getLanguage(), command.getDeviGuild().isAutoModAntiInvites() ? 302 : 303) + "\n`" + command.getDeviGuild().getPrefix() + "automod invites`\n\n",
                false);
        //advertising
        builder.addField(Emote.ADVERTISING + " " + Translator.getTranslation(command.getLanguage(), 77),
                Translator.getTranslation(command.getLanguage(), command.getDeviGuild().isAutoModAntiAds() ? 302 : 303) + "\n`" + command.getDeviGuild().getPrefix() + "automod advertising`\n\n",
                false);
        //spam
        builder.addField(Emote.SPAM + " " + Translator.getTranslation(command.getLanguage(), 632),
                Translator.getTranslation(command.getLanguage(), command.getDeviGuild().isAutoModAntiSpam() ? 302 : 303) + "\n`" + command.getDeviGuild().getPrefix() + "automod spam`\n\n",
                false);
        //mass mention
        builder.addField(Emote.MENTION + " " + Translator.getTranslation(command.getLanguage(), 633),
                Translator.getTranslation(command.getLanguage(), command.getDeviGuild().isAutoModAntiMassMention() ? 302 : 303) + "\n`" + command.getDeviGuild().getPrefix() + "automod mentions`\n\n",
                false);

        return builder.build();
    }*/
}
