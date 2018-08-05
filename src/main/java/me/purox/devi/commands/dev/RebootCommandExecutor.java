package me.purox.devi.commands.dev;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.waiter.WaitingResponse;
import me.purox.devi.core.waiter.WaitingResponseBuilder;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import me.purox.devi.utils.MessageUtils;
import me.purox.devi.utils.TimeUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static me.purox.devi.core.Devi.rebootTime;

public class RebootCommandExecutor implements CommandExecutor {

    private Devi devi;

    public RebootCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    private boolean isRebooting = false;

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (!devi.getAdmins().contains(sender.getId())) return;

        if (args.length == 0) {
            String time = TimeUtils.toRelative(new Date(System.currentTimeMillis()), rebootTime);
            sender.reply(DeviEmote.ERROR + " | Please enter the amount minutes until Devi will reboot or use `--urgent` for urgent reboot. \n" +
                    "\uD83D\uDCC6 | Devi is scheduled to automatically reboot in `" + time.substring(0, time.length() -4) + "`.");
            return;
        }
        if(isRebooting){
            sender.reply(DeviEmote.ERROR + " | Devi is already rebooting.");
            return;

        }
        if (args[0].equalsIgnoreCase("--urgent")) {
            devi.reboot(1, command.getEvent().getChannel());
            sender.reply(DeviEmote.SUCCESS + " | **Urgent Reboot**: Devi will be rebooting in 1 minute.");
            isRebooting = true;
            return;
        }

        int minutes = Integer.parseInt(args[0]);

        sender.reply(DeviEmote.SUCCESS + " | Devi will be rebooting in " + minutes + " minute" + (minutes == 1 ? "" : "s"));
        devi.reboot(minutes, command.getEvent().getChannel());
        isRebooting = true;
    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 0;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return null;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.DEV;
    }
}
