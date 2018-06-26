package me.purox.devi.commands.dev;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import org.bson.Document;

import java.util.List;

public class GuildDataCommandExecutor implements CommandExecutor {

    private Devi devi;

    public GuildDataCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (!devi.getAdmins().contains(sender.getId()) && !sender.isConsoleCommandSender()) return;

        Message message = MessageUtils.sendMessageSync(command.getEvent().getChannel(), "Collecting guild data..");
        if (message == null) return;

        DeviGuild deviGuild = args.length == 0 ? command.getDeviGuild() : new DeviGuild(args[0], devi);
        StringBuilder builder = new StringBuilder();

        builder.append("/*-----------------------*/\n");
        builder.append("/*-------~Commands~------*/\n");
        builder.append("/*-----------------------*/\n\n");


        for (Document document : deviGuild.getCommands()) {
            builder.append("------------------------\n");
            builder.append("Unique ID: ").append(document.get("_id")).append("\n");
            builder.append("Invoke: ").append(document.get("invoke")).append("\n");
            builder.append("Response: ").append(document.get("response")).append("\n");
            builder.append("------------------------\n\n");
        }

        builder.append("/*-----------------------*/\n");
        builder.append("/*-------~Streams~-------*/\n");
        builder.append("/*-----------------------*/\n\n");

        for (Document document : deviGuild.getStreams()) {
            builder.append("------------------------\n");
            builder.append("Unique ID: ").append(document.get("_id")).append("\n");
            builder.append("Stream ID: ").append(document.get("stream")).append("\n");
            builder.append("------------------------\n\n");
        }

        builder.append("/*-----------------------*/\n");
        builder.append("/*-------~Settings~------*/\n");
        builder.append("/*-----------------------*/\n\n");

        for (GuildSettings.Settings setting : GuildSettings.Settings.values()) {
            builder.append("------------------------\n");
            builder.append("Key: ").append(setting.name()).append(" \n");
            builder.append("Value: ").append(deviGuild.getSettings().getValue(setting)).append(" \n");
            builder.append("------------------------\n\n");
        }

        new RequestBuilder(devi.getOkHttpClient()).setURL("https://hastebin.com/documents").setRequestType(Request.RequestType.POST)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .setStringBody(builder.toString())
                .build()
                .asJSON(res -> message.editMessage("Collected guild settings: https://hastebin.com/" + res.getBody().getString("key")).queue(),
                        failure -> message.editMessage("Failed to upload guild settings to hastebin.").queue());

    }

    @Override
    public boolean guildOnly() {
        return true;
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
        return null;
    }
}
