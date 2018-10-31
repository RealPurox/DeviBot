package me.purox.devi.commands.admin;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.AnimatedEmote;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.utils.TimeUtils;

import java.util.Date;

public class RebootCommand extends ICommand {

    private Devi devi;
    private boolean rebooting = false;

    public RebootCommand(Devi devi) {
        super("reboot");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (!devi.getAdmins().contains(sender.getId())) return;

        if (command.getArgs().length == 0) {
            String time = TimeUtils.toRelative(new Date(System.currentTimeMillis()), devi.getRebootTime());
            sender.reply(Emote.ERROR + " | Please enter the amount minutes until Devi will reboot or use `--urgent` for urgent reboot. \n" +
                    "\uD83D\uDCC6 | Devi is scheduled to automatically reboot in `" + time.substring(0, time.length() -4) + "`.");
            return;
        }
        if(rebooting){
            sender.reply(Emote.ERROR + " | Devi is already rebooting. " + new AnimatedEmote(devi).EvilParrot().getAsMention());
            return;

        }
        if (command.getArgs()[0].equalsIgnoreCase("--urgent")) {
            devi.reboot(1, command.getChannel());
            sender.reply(Emote.SUCCESS + " | **Urgent Reboot**: Devi will be rebooting in 1 minute.");
            rebooting = true;
            return;
        }

        int minutes = Integer.parseInt(command.getArgs()[0]);

        sender.reply(Emote.SUCCESS + " | Devi will be rebooting in " + minutes + " minute" + (minutes == 1 ? "" : "s"));
        devi.reboot(minutes, command.getChannel());
        rebooting = true;
    }
}
