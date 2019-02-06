package net.devibot.provider.commands.management;

import net.devibot.core.entities.automod.AutoModAntiInvites;
import net.devibot.core.utils.JavaUtils;
import net.devibot.provider.commands.CommandSender;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.core.DiscordBot;
import net.devibot.core.entities.Emote;
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
            sender.message().setEmbed(getDefaultEmbed(command)).execute();
            return;
        }

        Boolean bool = JavaUtils.getBoolean(args[0]);
        //its a boolean so enable / disable auto-mod
        if (bool != null) {
            command.getDeviGuild().getAutoMod().setEnabled(bool);
            discordBot.getMainframeManager().saveDeviGuild(command.getDeviGuild());
            sender.message().append(Emote.AUTO_MOD).append(" | ").appendTranslation(command.getDeviGuild().getAutoMod().isEnabled() ? "commands.automod.update.enabled" : "commands.automod.update.disabled").execute();
            return;
        }

        //<editor-fold desc="invites">
        if (args[0].equalsIgnoreCase("invites")) {

            AutoModAntiInvites antiInvites = command.getDeviGuild().getAutoMod().getAntiInvites();

            EmbedBuilder builder = new EmbedBuilder()
                    .setColor(0x7289DA)
                    .setAuthor(Translator.getTranslation(command.getLanguage(), "commands.automod.settings.title"));

            builder.setDescription(Emote.INVITE + " | " + Translator.getTranslation(command.getLanguage(), "commands.automod.invites.description"));

            builder.addField(Translator.getTranslation(command.getLanguage(), "general.status"), antiInvites.isEnabled()
                            ? (Emote.SUCCESS + " | " + Translator.getTranslation(command.getLanguage(), "commands.automod.module.status.enabled", "\n", "`" + command.getPrefix() + "automod invites off`"))
                            : (Emote.ERROR + " | " + Translator.getTranslation(command.getLanguage(), "commands.automod.module.status.disabled", "\n", "`" + command.getPrefix() + "automod invites on`")), false);

            builder.addField(Translator.getTranslation(command.getLanguage(), "general.status"), antiInvites.isStrikes()
                    ? Emote.SUCCESS + " | This module will strike users when it detects them.\nUse `" + command.getPrefix() + "automod invites strikes off` to disable strikes for this module."
                    : Emote.ERROR + " | This module won't strike users.\nUse `" + command.getPrefix() + "automod invites strikes on` to enable strikes for this module.", false);

            builder.addField(Translator.getTranslation(command.getLanguage(), "general.status"), antiInvites.isStrikes()
                    ? Emote.SUCCESS + " | " + Translator.getTranslation(command.getLanguage(), "commands.automod.module.strikes.enabled", "\n", "`" + command.getPrefix() + "automod invites strikes off`")
                    : Emote.ERROR + " | " + Translator.getTranslation(command.getLanguage(), "commands.automod.module.strikes.disabled", "\n", "`" + command.getPrefix() + "automod invites strikes on`"), false);

            if (args.length == 1) {
                sender.message().setEmbed(builder.build()).execute();
                return;
            }

            if (args.length == 2) {
                Boolean antiInvitesBool = JavaUtils.getBoolean(args[1]);
                if (antiInvitesBool == null) {
                    sender.errorMessage().appendTranslation("commands.general.error.arguments", "`" + command.getPrefix() + "automod invites <on/off>`").execute();
                    return;
                }

                antiInvites.setEnabled(antiInvitesBool);
                discordBot.getMainframeManager().saveDeviGuild(command.getDeviGuild());
                sender.message().append(Emote.AUTO_MOD).append(" | ").appendTranslation(antiInvites.isEnabled() ? "commands.automod.invites.update.enabled" : "commands.automod.invites.update.disabled").execute();
                return;
            }

            if (args[1].equalsIgnoreCase("strikes")) {
                Boolean antiInvitesStrikeBool = JavaUtils.getBoolean(args[2]);
                if (antiInvitesStrikeBool == null) {
                    sender.errorMessage().appendTranslation("commands.general.error.arguments", "`" + command.getPrefix() + "automod invites strikes <on/off>`").execute();
                    return;
                }

                antiInvites.setStrikes(antiInvitesStrikeBool);
                discordBot.getMainframeManager().saveDeviGuild(command.getDeviGuild());
                sender.message().append(Emote.AUTO_MOD).appendTranslation(antiInvites.isStrikes() ? "commands.automod.invites.strikes.enabled" : "commands.automod.invites.strikes.disabled").execute();
                return;
            }
        }
        //</editor-fold>

        //if everything fails
        sender.message().setEmbed(getDefaultEmbed(command)).execute();
    }

    private MessageEmbed getDefaultEmbed(Command command) {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(0x7289DA)
                .setAuthor(Translator.getTranslation(command.getLanguage(), "commands.automod.settings.title"));

        if (command.getDeviGuild().getAutoMod().isEnabled())
            builder.setDescription(Translator.getTranslation(command.getLanguage(), "commands.automod.status.enabled", "`" + command.getDeviGuild().getPrefix() + "automod off`") + " " + Emote.AUTO_MOD);
        else builder.setDescription(Translator.getTranslation(command.getLanguage(), "commands.automod.status.disabled",  "`" + command.getDeviGuild().getPrefix() + "automod on`") + " " + Emote.AUTO_MOD);

        //invite links
        builder.addField(Emote.INVITE + " " + Translator.getTranslation(command.getLanguage(), "commands.automod.invites.title"),
                Translator.getTranslation(command.getLanguage(), command.getDeviGuild().getAutoMod().getAntiInvites().isEnabled() ? "general.status.enabled" : "general.status.disabled") + "\n`" + command.getDeviGuild().getPrefix() + "automod invites`\n\n",
                false);
        //advertising
        builder.addField(Emote.ADVERTISING + " " + Translator.getTranslation(command.getLanguage(), "commands.automod.advertisement.title"),
                Translator.getTranslation(command.getLanguage(), command.getDeviGuild().getAutoMod().getAntiAdvertising().isEnabled() ? "general.status.enabled" : "general.status.disabled") + "\n`" + command.getDeviGuild().getPrefix() + "automod advertising`\n\n",
                false);
        //spam
        builder.addField(Emote.SPAM + " " + Translator.getTranslation(command.getLanguage(), "commands.automod.spam.title"),
                Translator.getTranslation(command.getLanguage(), command.getDeviGuild().getAutoMod().getAntiSpam().isEnabled() ? "general.status.enabled" : "general.status.disabled") + "\n`" + command.getDeviGuild().getPrefix() + "automod spam`\n\n",
                false);
        //mass mention
        builder.addField(Emote.MENTION + " " + Translator.getTranslation(command.getLanguage(), "commands.automod.mention.title"),
                Translator.getTranslation(command.getLanguage(), command.getDeviGuild().getAutoMod().getAntiMassMention().isEnabled() ? "general.status.enabled" : "general.status.disabled") + "\n`" + command.getDeviGuild().getPrefix() + "automod mentions`\n\n",
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
            discordBot.getMainframeManager().saveDeviGuild(command.getDeviGuild());
            sender.reply(Emote.AUTO_MOD +  " | " + Translator.getTranslationOLD(command.getLanguage(), command.getDeviGuild().isAutoModEnabled() ? 286 : 287));
            return;
        }

        if (args[0].equalsIgnoreCase("invites")) {

            EmbedBuilder builder = new EmbedBuilder()
                    .setColor(0x7289DA)
                    .setAuthor(Translator.getTranslationOLD(command.getLanguage(), 631));

            builder.setDescription("This Auto-Mod module will delete discord invite links and give the sender a strike, if strikes are enabled for this module.");
            builder.addField("Status", command.getDeviGuild().isAutoModAntiInvites() ? Emote.SUCCESS + " | This module is currently enabled" : Emote.ERROR + " | This module is currently disabled" , false);

            sender.reply(builder.build());


        } else {
            sender.reply(getDefaultEmbed(command));
        }
    }*/
}
