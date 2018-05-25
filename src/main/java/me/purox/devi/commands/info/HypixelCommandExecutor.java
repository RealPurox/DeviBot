package me.purox.devi.commands.info;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.Permission;
import org.json.JSONObject;

import java.util.List;

public class HypixelCommandExecutor implements CommandExecutor {

    private Devi devi;
    public HypixelCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length == 0) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "hypixel <player>`"));
            return;
        }

        String search = args[0];
        try {
            String URL = "https://api.hypixel.net/player?key=" + devi.getSettings().getHypixelAPIKey() + "&name=" + search;
            JSONObject data = Unirest.get(URL).asJson().getBody().getObject();

            if (data.get("player") == null) {
                sender.reply("not found");
                return;
            }

            JSONObject player = data.getJSONObject("player");
            System.out.println(player.getJSONArray("achievementsOneTime").length());

            System.out.println(data);
        } catch (UnirestException e) {
            sender.reply(devi.getTranslation(command.getLanguage(), 217));
        }
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
}
