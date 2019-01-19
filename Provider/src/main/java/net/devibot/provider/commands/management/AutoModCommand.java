package net.devibot.provider.commands.management;

import net.devibot.core.entities.automod.AutoModAntiInvites;
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

        Boolean bool = JavaUtils.getBoolean(args[0]);
        //its a boolean so enable / disable auto-mod
        if (bool != null) {
            command.getDeviGuild().getAutoMod().setEnabled(bool);
            discordBot.getMainframeManager().requestDeviGuildSettingsSave(command.getDeviGuild());
            sender.reply(Emote.AUTO_MOD +  " | " + Translator.getTranslation(command.getLanguage(), command.getDeviGuild().getAutoMod().isEnabled() ? 286 : 287));
            return;
        }

        //TODO TRANSLATIONS

        //<editor-fold desc="invites">
        if (args[0].equalsIgnoreCase("invites")) {

            AutoModAntiInvites antiInvites = command.getDeviGuild().getAutoMod().getAntiInvites();

            EmbedBuilder builder = new EmbedBuilder()
                    .setColor(0x7289DA)
                    .setAuthor(Translator.getTranslation(command.getLanguage(), 631));

            builder.setDescription(Emote.INVITE + " | This Auto-Mod module will delete discord invite links and give the sender a strike, if strikes are enabled for this module.");

            builder.addField("Status", antiInvites.isEnabled() ? Emote.SUCCESS + " | This module is currently enabled.\nUse `" + command.getPrefix() + "automod invites off` to disable it." : Emote.ERROR + " | This module is currently disabled.\nUse `" + command.getPrefix() + "automod invites on` to enabled it.", false);
            builder.addField("Strikes", antiInvites.isStrikes() ? Emote.SUCCESS + " | This module will strike users when it detects them.\nUse `" + command.getPrefix() + "automod invites strikes off` to disable strikes for this module." : Emote.ERROR + " | This module won't strike users.\nUse `" + command.getPrefix() + "automod invites strikes on` to enable strikes for this module.", false);

            if (args.length == 1) {
                sender.reply(builder.build());
                return;
            }

            if (args.length == 2) {
                Boolean antiInvitesBool = JavaUtils.getBoolean(args[1]);
                if (antiInvitesBool == null) {
                    sender.reply(Emote.ERROR + " | " + Translator.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "automod invites <on/off>`"));
                    return;
                }

                antiInvites.setEnabled(antiInvitesBool);
                discordBot.getMainframeManager().requestDeviGuildSettingsSave(command.getDeviGuild());
                sender.reply(Emote.AUTO_MOD + " | " + Translator.getTranslation(command.getLanguage(), antiInvites.isEnabled() ? 280 : 281));
                return;
            }

            if (args[1].equalsIgnoreCase("strikes")) {
                Boolean antiInvitesStrikeBool = JavaUtils.getBoolean(args[2]);
                if (antiInvitesStrikeBool == null) {
                    sender.reply(Emote.ERROR + " | " + Translator.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "automod invites strikes <on/off>`"));
                    return;
                }

                antiInvites.setStrikes(antiInvitesStrikeBool);
                discordBot.getMainframeManager().requestDeviGuildSettingsSave(command.getDeviGuild());
                sender.reply(antiInvites.isStrikes() ? Emote.AUTO_MOD + " | This module will now strike users." : Emote.AUTO_MOD + " | This module will no longer strike users.");
                return;
            }
        }
        //</editor-fold>



        //if everything fails
        sender.reply(getDefaultEmbed(command));
    }

    private MessageEmbed getDefaultEmbed(Command command) {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(0x7289DA)
                .setAuthor(Translator.getTranslation(command.getLanguage(), 74));

        if (command.getDeviGuild().getAutoMod().isEnabled())
            builder.setDescription(Translator.getTranslation(command.getLanguage(), 629,  "`" + command.getDeviGuild().getPrefix() + "automod off`") + " " + Emote.AUTO_MOD);
        else builder.setDescription(Translator.getTranslation(command.getLanguage(), 630,  "`" + command.getDeviGuild().getPrefix() + "automod on`") + " " + Emote.AUTO_MOD);

        //invite links
        builder.addField(Emote.INVITE + " " + Translator.getTranslation(command.getLanguage(), 631),
                Translator.getTranslation(command.getLanguage(), command.getDeviGuild().getAutoMod().getAntiInvites().isEnabled() ? 302 : 303) + "\n`" + command.getDeviGuild().getPrefix() + "automod invites`\n\n",
                false);
        //advertising
        builder.addField(Emote.ADVERTISING + " " + Translator.getTranslation(command.getLanguage(), 77),
                Translator.getTranslation(command.getLanguage(), command.getDeviGuild().getAutoMod().getAntiAdvertising().isEnabled() ? 302 : 303) + "\n`" + command.getDeviGuild().getPrefix() + "automod advertising`\n\n",
                false);
        //spam
        builder.addField(Emote.SPAM + " " + Translator.getTranslation(command.getLanguage(), 632),
                Translator.getTranslation(command.getLanguage(), command.getDeviGuild().getAutoMod().getAntiSpam().isEnabled() ? 302 : 303) + "\n`" + command.getDeviGuild().getPrefix() + "automod spam`\n\n",
                false);
        //mass mention
        builder.addField(Emote.MENTION + " " + Translator.getTranslation(command.getLanguage(), 633),
                Translator.getTranslation(command.getLanguage(), command.getDeviGuild().getAutoMod().getAntiMassMention().isEnabled() ? 302 : 303) + "\n`" + command.getDeviGuild().getPrefix() + "automod mentions`\n\n",
                false);

        return builder.build();
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
    }*/
}
