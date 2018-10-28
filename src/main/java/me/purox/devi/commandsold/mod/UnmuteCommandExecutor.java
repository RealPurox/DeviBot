package me.purox.devi.commandsold.mod;

import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandExecutor;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.punishments.Punishment;
import me.purox.devi.punishments.PunishmentBuilder;
import me.purox.devi.punishments.options.MuteOptions;
import me.purox.devi.utils.DiscordUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UnmuteCommandExecutor implements CommandExecutor {

    private Devi devi;
    public UnmuteCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, ICommand command, CommandSender sender) {
        if (args.length < 1) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "unban <user> [reason]`\n\n" +
                    devi.getTranslation(command.getLanguage(), 610, "`[reason]`")));
            return;
        }

        User user = DiscordUtils.getUser(args[0], command.getEvent().getGuild());
        if (user == null) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 13, "`" + args[0] + "`"));
            return;
        }
        Member member = command.getEvent().getGuild().getMember(user);

        String roleId = command.getDeviGuild().getSettings().getStringValue(GuildSettings.Settings.MUTE_ROLE);
        if (member.getRoles().stream().noneMatch(role -> role.getId().equals(roleId))) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 52));
            return;
        }

        if(!PermissionUtil.canInteract(command.getEvent().getMember(), command.getEvent().getGuild().getMemberById(user.getId())) ||
                user.getId().equals(command.getEvent().getJDA().getSelfUser().getId()) || user.getId().equals(sender.getId())){
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 53));
            return;
        }

        if(!PermissionUtil.checkPermission(command.getEvent().getGuild().getSelfMember(), Permission.MANAGE_ROLES) ||
                !PermissionUtil.canInteract(command.getEvent().getGuild().getSelfMember(), command.getEvent().getGuild().getMemberById(user.getId()))){
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 54));
            return;
        }

        String reason = args.length == 1 ? "Unknown reason" : Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        //the role will never be null because we check above if they have a role matching our ID
        Role role = command.getEvent().getGuild().getRoleById(roleId);
        new PunishmentBuilder(command.getDeviGuild())
                .setOptions(new MuteOptions().setRole(role))
                .setPunisher(sender)
                .setPunished(user)
                .setType(Punishment.Type.UNMUTE)
                .setReason(reason)
                .build()
                .execute(success -> {
                    sender.reply(Emote.SUCCESS + " | " + devi.getTranslation(command.getLanguage(), 611, "`" + user.getName() + "#" + user.getDiscriminator() + "`"));
                }, error -> {
                    sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 612));
                });
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 44;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return Permission.VOICE_MUTE_OTHERS;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.MODERATION;
    }
}
