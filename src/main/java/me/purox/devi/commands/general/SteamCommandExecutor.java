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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SteamCommandExecutor implements CommandExecutor {

    private Devi devi;
    private Pattern steamIdPattern;
    public SteamCommandExecutor(Devi devi) {
        this.devi = devi;
        this.steamIdPattern = Pattern.compile("^[0-9]{17}$");

    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length == 0) {
            sender.reply("Please enter your profile ID or Steam ID!");
            return;
        }

        String search = args[0];
        Matcher matcher = steamIdPattern.matcher(search);
        long steamId;

        // -- name to id --
        if (!matcher.matches()) {
            String getSteamIDFromURL = "http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?key=" + devi.getSettings().getSteamApiKey() + "&vanityurl=" + search;
            System.out.println(getSteamIDFromURL);
            JSONObject response = new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(getSteamIDFromURL).build().asJSONSync().getBody();
            JSONObject id = response.getJSONObject("response");

            if (id.getInt("success") != 1) {
                sender.reply("I couldn't find that user name.");
                System.out.println("DID NOT FIND THE STEAM name.");
                return;
            }

            steamId = id.getLong("steamid");
            System.out.println("FOUND THE STEAM ID. BUT ALSO CHECKING IF THE MESSAGE WAS AN ID!");
        }
        // -- check if id exists --
        else {
            String getSteamIFromID = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + devi.getSettings().getSteamApiKey() + "&steamids=" + search;
            JSONObject response2 = new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(getSteamIFromID).build().asJSONSync().getBody();
            JSONObject id = response2.getJSONObject("response");

            System.out.println(response2);

            if (id.getInt("success") != 1) {
                sender.reply("I couldn't find that user ID.");
                System.out.println("DID NOT FIND THE STEAM ID.");
                return;
            }
            steamId = id.getLong("steamid");
            System.out.println("FOUND THE STEAM ID. NOW GETTING THE PROFILE.");
        }


        // -- get profile --
        String findProfile = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + devi.getSettings().getSteamApiKey() + "&steamids=" + steamId;
        System.out.println(findProfile);
        System.out.println("yo");
        JSONObject profileResponse = new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(findProfile).build().asJSONSync().getBody();
        JSONObject players = (JSONObject) profileResponse.getJSONObject("response").getJSONArray("players").get(0);

        System.out.println(profileResponse.getJSONObject("response"));

        long steamID = players.getLong("steamid");
        String countryCode = players.getString("loccountrycode");

        sender.reply("Your Profile: " + players + "\n\n<@161494492422078464> I'm sorry for being rude to you you but I helped you with this so know we're best friends again :kiss:");

        EmbedBuilder embed = new EmbedBuilder();
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