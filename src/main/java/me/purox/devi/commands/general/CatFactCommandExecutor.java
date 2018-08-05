package me.purox.devi.commands.general;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import net.dv8tion.jda.core.Permission;
import org.json.JSONObject;

import java.util.List;

public class CatFactCommandExecutor implements CommandExecutor {

    private Devi devi;
    public CatFactCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        String URL = "https://catfact.ninja/fact";
        new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(URL).build()
                .asJSON(success -> {
                    JSONObject body = success.getBody();
                    if(success.getStatus() == 404 || success.getBody() == null){
                        sender.reply(DeviEmote.ERROR + "| " + devi.getTranslation(command.getLanguage(), 217));
                        return;
                    }
                    sender.reply("**"+sender.getName()+"**" + ", " + body.getString("fact"));
                });

    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 537;
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
