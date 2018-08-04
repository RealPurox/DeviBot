package me.purox.devi.commands.general;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class CatCommandExecutor implements CommandExecutor {

    private Devi devi;

    public CatCommandExecutor(Devi devi)
    {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        String URL = "https://cataas.com/cat";
        new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(URL).build()
                .asString(success -> {
                    if(success.getStatus() == 404 || success.getBody() == null){
                        sender.reply(DeviEmote.ERROR + "| " + devi.getTranslation(command.getLanguage(), 217));
                        return;
                    }
                    MessageBuilder message = new MessageBuilder();
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle(devi.getTranslation(command.getLanguage(), 540));
                    try {
                        InputStream file = new URL(URL).openStream();
                        embed.setImage("attachment://cat.png");
                        message.setEmbed(embed.build());
                        command.getEvent().getChannel().sendFile(file, "cat.png", message.build()).queue();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 541;
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
        return ModuleType.FUN_COMMANDS;
    }
}
