package me.purox.devi.commands.mod;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.punishments.Punishment;
import me.purox.devi.punishments.PunishmentBuilder;
import me.purox.devi.punishments.options.VoiceKickOptions;
import me.purox.devi.utils.DiscordUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.Arrays;
import java.util.stream.Collectors;

public class VoiceKickCommand extends ICommand {

    private Devi devi;

    public VoiceKickCommand(Devi devi) {
        super("voicekick", "vkick");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        String[] args = command.getArgs();

        if(args.length < 1){
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "voicekick <user> [reason]`\n\n" +
                    devi.getTranslation(command.getLanguage(), 605, "`[reason]`")));
            return;
        }

        User user = DiscordUtils.getUser(args[0], command.getGuild());
        if (user == null) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 13, "**" + args[0] + "**"));
            return;
        }

        Member member = command.getGuild().getMember(user);
        if (!PermissionUtil.canInteract(command.getMember(), member) || user.getId().equals(sender.getId()) ||
                command.getJDA().getSelfUser().getId().equals(sender.getId())) {
            sender.reply(devi.getTranslation(command.getLanguage(), 528));
            return;
        }

        if (sender.getId().equalsIgnoreCase(user.getId()) ||
                !PermissionUtil.checkPermission(command.getGuild().getSelfMember(), Permission.KICK_MEMBERS) ||
                !PermissionUtil.canInteract(command.getGuild().getSelfMember(), member)) {
            sender.reply(devi.getTranslation(command.getLanguage(), 529));
            return;
        }

        if (!member.getVoiceState().inVoiceChannel()) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 574, "**" + user.getName() + "**"));
            return;
        }

        if (!command.getGuild().getSelfMember().hasPermission(Permission.VOICE_MOVE_OTHERS)) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 580, "`" + devi.getTranslation(command.getLanguage(), 581) + "`"));
            return;
        }

        if (!command.getGuild().getSelfMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 580, "`" + devi.getTranslation(command.getLanguage(), 582) + "`"));
            return;
        }

        if (!command.getGuild().getSelfMember().hasPermission(Permission.VOICE_CONNECT)) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 580, "`" + devi.getTranslation(command.getLanguage(), 583) + "`"));
            return;
        }

        if (!member.hasPermission(Permission.VOICE_CONNECT)) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 585, "**" + member.getUser().getName() + "**",
                    "`" + devi.getTranslation(command.getLanguage(), 583) + "`"));
            return;
        }

        String reason = args.length == 1 ? "Unknown reason" : Arrays.stream(args).skip(1).collect(Collectors.joining(" "));

        new PunishmentBuilder(command.getDeviGuild())
                .setReason(reason)
                .setType(Punishment.Type.VOICEKICK)
                .setPunished(user)
                .setPunisher(sender)
                .setOptions(new VoiceKickOptions().setChannel(member.getVoiceState().getChannel()))
                .build()
                .execute(success -> {
                    sender.reply(Emote.SUCCESS + " | " + devi.getTranslation(command.getLanguage(), 620, "`" + user.getName() + "#" + user.getDiscriminator() + "`"));
                }, error -> {
                    sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 617));
                });

    }
}
