package me.purox.devi.commands.admin;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.guild.entities.Command;
import me.purox.devi.core.guild.entities.Stream;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Message;

public class GuildDataCommand extends ICommand {

    private Devi devi;

    public GuildDataCommand(Devi devi) {
        super("guilddata");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (!devi.getAdmins().contains(sender.getId())) return;

        Message message = MessageUtils.sendMessageSync(command.getChannel(), "Collecting guild data..");
        if (message == null) return;

        DeviGuild deviGuild = command.getArgs().length == 0 ? command.getDeviGuild() : new DeviGuild(command.getArgs()[0], devi);
        StringBuilder builder = new StringBuilder();

        builder.append("/*-----------------------*/\n");
        builder.append("/*-------~Commands~------*/\n");
        builder.append("/*-----------------------*/\n\n");


        for (me.purox.devi.core.guild.entities.Command commandEntity : deviGuild.getCommandEntities()) {
            builder.append("------------------------\n");
            builder.append("Unique ID: ").append(commandEntity.get_id()).append("\n");
            builder.append("Invoke: ").append(commandEntity.getInvoke()).append("\n");
            builder.append("Response: ").append(commandEntity.getResponse()).append("\n");
            builder.append("------------------------\n\n");
        }

        builder.append("/*-----------------------*/\n");
        builder.append("/*-------~Streams~-------*/\n");
        builder.append("/*-----------------------*/\n\n");

        for (Stream stream : deviGuild.getStreams()) {
            builder.append("------------------------\n");
            builder.append("Unique ID: ").append(stream.get_id()).append("\n");
            builder.append("Stream ID: ").append(stream.getStream()).append("\n");
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
}
