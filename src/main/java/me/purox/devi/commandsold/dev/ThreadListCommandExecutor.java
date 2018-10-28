package me.purox.devi.commandsold.dev;

import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandExecutor;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import net.dv8tion.jda.core.Permission;

import java.util.List;

public class ThreadListCommandExecutor implements CommandExecutor {

    private Devi devi;

    public ThreadListCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, ICommand command, CommandSender sender) {
        if (!devi.getAdmins().contains(sender.getId())) return;

        StringBuilder builder = new StringBuilder();
        builder.append("id -> name - thread group name - thread state\n");

        int t = 1;
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            builder.append(t++).append(" -> ").append(thread.getName()).append(" - ").append(thread.getThreadGroup().getName()).append(" - ").append(thread.getState()).append("\n");
        }


        new RequestBuilder(devi.getOkHttpClient()).setURL("https://hastebin.com/documents").setRequestType(Request.RequestType.POST)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .setStringBody(builder.toString())
                .build()
                .asJSON(res -> sender.reply("Thread list: https://hastebin.com/" + res.getBody().getString("key")));

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

