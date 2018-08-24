package me.purox.devi.commands.general;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("ALL")
public class DogCommandExecutor implements CommandExecutor {

    private Devi devi;

    public DogCommandExecutor(Devi devi){
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        String URL = "https://dog.ceo/api/breeds/image/random";
        new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(URL).build()
                .asJSON(success -> {
                    JSONObject body = success.getBody();
                    if(success.getStatus() == 404 || success.getBody() == null){
                        sender.reply(Emote.ERROR + "| " + devi.getTranslation(command.getLanguage(), 217));
                        return;
                    }
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle(devi.getTranslation(command.getLanguage(), 538));
                    embed.setImage(body.getString("message"));
                    sender.reply(embed.build());
                });
    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 539;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("doggo");
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
