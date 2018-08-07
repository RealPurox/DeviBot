package me.purox.devi.commands.mod;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.utils.DiscordUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class VoiceKickCommandExecutor implements CommandExecutor {

    private Devi devi;

    public VoiceKickCommandExecutor(Devi devi){
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if(args.length < 2){
            sender.reply(Emote.ERROR + " | Invalid arguments. !voicekick <user> [reason]");
            return;
        }

        User user = DiscordUtils.getUser(args[0], command.getEvent().getGuild());
        if(user == null){
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 13, "**" + args[0] + "**"));
            return;
        }

        Member member = command.getEvent().getGuild().getMember(user);
        if (!PermissionUtil.canInteract(command.getEvent().getMember(), member) || user.getId().equals(sender.getId()) || command.getEvent().getJDA().getSelfUser().getId().equals(sender.getId())) {
            sender.reply( devi.getTranslation(command.getLanguage(), 528));
            return;
        }

        if (sender.getId().equalsIgnoreCase(user.getId()) ||
                !PermissionUtil.checkPermission(command.getEvent().getGuild().getSelfMember(), Permission.KICK_MEMBERS) ||
                !PermissionUtil.canInteract(command.getEvent().getGuild().getSelfMember(), member)) {
            sender.reply( devi.getTranslation(command.getLanguage(), 529));
            return;
        }
        if(!member.getVoiceState().inVoiceChannel()){
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 574, "**" + user.getName() + "**"));
            return;
        }

        String reason = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        VoiceChannel connectedChannel = member.getVoiceState().getChannel();
        String id = command.getEvent().getGuild().getController().createVoiceChannel("devitemp").complete().getId(); // currently only way to do this, hopefully discord fixes a way to kick ppl from voice channels
        VoiceChannel temp = command.getEvent().getGuild().getVoiceChannelById(id);
        command.getEvent().getGuild().getController().moveVoiceMember(member, temp).queue(
                success -> {
                devi.getModLogManager().logVoiceKick(command.getDeviGuild(), member, command.getEvent().getMember(), connectedChannel, reason);

                sender.reply(Emote.SUCCESS + " | " + devi.getTranslation(command.getLanguage(), 575, "**" + user.getName() + "**", "`" + connectedChannel.getName() + "`", "`" + reason + "`"));
                temp.delete().queue();
                },
                error -> sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 531))
        );
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 576;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("vckick");
    }

    @Override
    public Permission getPermission() {
        return Permission.KICK_MEMBERS;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.MODERATION;
    }
}
