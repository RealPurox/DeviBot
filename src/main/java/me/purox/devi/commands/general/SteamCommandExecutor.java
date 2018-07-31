package me.purox.devi.commands.general;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class SteamCommandExecutor implements CommandExecutor {

    private Devi devi;
    public SteamCommandExecutor(Devi devi) {
        this.devi = devi;
    }
    @SuppressWarnings("Duplicates")
    @Override
    public void execute(String[] args, Command command, CommandSender sender) {

        if (args.length == 0) {
            sender.reply("Please enter your profile ID or Steam ID!");
            return;
        }

        String search = args[0];
        AtomicLong steamId = new AtomicLong();

        if (!isLong(search)) {
            // Getting steam id from custom url
            String getSteamIDFromURL = "http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?key=" + devi.getSettings().getSteamApiKey() + "&vanityurl=" + search;

            new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(getSteamIDFromURL).build().asJSON(response -> {
                JSONObject id = response.getBody().getJSONObject("response");

                if (id.getInt("success") != 1) {
                    sender.reply("I couldn't find that user ID.");
                    System.out.println("DID NOT FIND THE STEAM ID.");
                    return;
                }else {
                    System.out.println(id.getLong("steamid"));
                    steamId.set(id.getLong("steamid"));
                    System.out.println("FOUND THE STEAM ID. BUT ALSO CHECKING IF THE MESSAGE WAS AN ID!");
                }
            });
        }else {
            String getSteamIFromID = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + devi.getSettings().getSteamApiKey() + "&steamids=" + search;

            new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(getSteamIFromID).build().asJSON(response2 -> {
                JSONObject id2 = response2.getBody().getJSONObject("response");

                if (id2.getInt("success") != 1) {
                    sender.reply("I couldn't find that user ID.");
                    System.out.println("DID NOT FIND THE STEAM ID.");
                    return;
                }else {
                    steamId.set(id2.getLong("steamid"));
                    System.out.println("FOUND THE STEAM ID. NOW GETTING THE PROFILE.");
                }
            });
        }

        String findProfile = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + devi.getSettings().getSteamApiKey() + "&steamids=" + steamId.toString();
        new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(findProfile).build().asJSON(profileResponse -> {
            System.out.println(profileResponse.getBody().getJSONObject("response"));
            JSONObject players = (JSONObject) profileResponse.getBody().getJSONObject("response").getJSONArray("players").get(0);

            long steamID = players.getJSONArray("players").getJSONObject(0).getLong("steamid");
            String countryCode = players.getJSONArray("players").getJSONObject(0).getString("loccountrycode");

            sender.reply("Your SteamID: " + steamID + "\n" + "Country Code: " + countryCode);
            System.out.println(steamID);

            EmbedBuilder embed = new EmbedBuilder();
        });
    }

    boolean isLong(String s) {
        try{
            Long.parseLong(s);
            return true;
        }catch (Exception e){
            return false;
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

    @Override
    public ModuleType getModuleType() {
        return null;
    }
}