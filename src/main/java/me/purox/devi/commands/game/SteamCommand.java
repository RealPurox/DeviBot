package me.purox.devi.commands.game;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import org.json.JSONObject;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SteamCommand extends ICommand {

    private Devi devi;
    private Pattern steamIdPattern;

    public SteamCommand(Devi devi) {
        super("steam");
        this.devi = devi;
        this.steamIdPattern = Pattern.compile("^[0-9]{17}$");
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (command.getArgs().length == 0) {
            sender.reply(Emote.ERROR + devi.getTranslation(command.getLanguage(), 493));
            return;
        }

        String search = command.getArgs()[0];
        Matcher matcher = steamIdPattern.matcher(search);
        long steamId;

        // -- name to id --
        if (!matcher.matches()) {
            String getSteamIDFromURL = "http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?key=" + devi.getSettings().getSteamApiKey() + "&vanityurl=" + search;
            JSONObject response = new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(getSteamIDFromURL).build().asJSONSync().getBody();
            JSONObject id = response.getJSONObject("response");

            if (id.getInt("success") != 1) {
                sender.reply(Emote.ERROR + devi.getTranslation(command.getLanguage(), 494));
                return;
            }

            steamId = id.getLong("steamid");
        }
        // -- check if id exists --
        else {
            String getSteamIFromID = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + devi.getSettings().getSteamApiKey() + "&steamids=" + search;
            JSONObject response = new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(getSteamIFromID).build().asJSONSync().getBody();
            JSONObject id = (JSONObject) response.getJSONObject("response").getJSONArray("players").get(0);

            System.out.println(response);

            if (id.length() <= 0) {
                sender.reply(Emote.ERROR + devi.getTranslation(command.getLanguage(), 495));
                return;
            }
            steamId = id.getLong("steamid");
        }

        // -- get profile --
        String findProfile = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + devi.getSettings().getSteamApiKey() + "&steamids=" + steamId;
        JSONObject profileResponse = new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(findProfile).build().asJSONSync().getBody();
        JSONObject players = (JSONObject) profileResponse.getJSONObject("response").getJSONArray("players").get(0);
        // -- get bans --

        String findBans = "http://api.steampowered.com/ISteamUser/GetPlayerBans/v1/?key=" + devi.getSettings().getSteamApiKey() + "&steamids=" + steamId;
        JSONObject banResponse = new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(findBans).build().asJSONSync().getBody();
        JSONObject bans = (JSONObject) banResponse.getJSONArray("players").get(0);

        String findUserLevel = "http://api.steampowered.com/IPlayerService/GetSteamLevel/v1/?key=" + devi.getSettings().getSteamApiKey() + "&steamid=" + steamId;
        JSONObject userLevelResponse = new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(findUserLevel).build().asJSONSync().getBody();
        JSONObject userLevel = (JSONObject) userLevelResponse.getJSONObject("response");

        long steamID = players.getLong("steamid");

        EmbedBuilder embed = new EmbedBuilder();
        embed.setThumbnail(players.getString("avatarfull"));
        embed.setAuthor(players.getString("personaname"), null, "https://i.imgur.com/ukoAGX1.png");
        embed.setColor(Color.decode("#1b2838"));

        embed.addField("**" + devi.getTranslation(command.getLanguage(), 496) + "**", (!(players.has("realname"))) ? devi.getTranslation(command.getLanguage(), 510) : players.getString("realname"), true);
        embed.addField("**" + devi.getTranslation(command.getLanguage(), 497) + "**", (!(players.has("loccountrycode"))) ? devi.getTranslation(command.getLanguage(), 510) : ":flag_" + players.getString("loccountrycode").toLowerCase() + ": " + players.getString("loccountrycode"), true);
        embed.addField("**" + devi.getTranslation(command.getLanguage(), 498) + "**", players.getString("profileurl"), false);

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date createdate = new Date(players.getLong("timecreated") * 1000L);
        Date lastlogoff = new Date(players.getLong("lastlogoff") * 1000L);

        embed.addField("**" + devi.getTranslation(command.getLanguage(), 499) + "**", format.format(createdate), true);
        embed.addField("**" + devi.getTranslation(command.getLanguage(), 500) + "**", format.format(lastlogoff), true);

        switch (players.getInt("personastate")) {
            case 0:
                embed.addField("**" + devi.getTranslation(command.getLanguage(), 501) + "**", Emote.OFFLINE + " Offline", true);
                break;
            case 1:
                embed.addField("**" + devi.getTranslation(command.getLanguage(), 501) + "**", Emote.ONLINE + " Online", true);
                break;
            case 2:
                embed.addField("**" + devi.getTranslation(command.getLanguage(), 501) + "**", Emote.DO_NOT_DISTURB + " Busy", true);
                break;
            case 3:
                embed.addField("**" + devi.getTranslation(command.getLanguage(), 501) + "**", Emote.AWAY + " Away", true);
                break;
            case 4:
                embed.addField("**" + devi.getTranslation(command.getLanguage(), 501) + "**", Emote.AWAY + " Snooze", true);
                break;
            case 5:
                embed.addField("**" + devi.getTranslation(command.getLanguage(), 501) + "**", Emote.ONLINE + " Looking to Trade", true);
                break;
            case 6:
                embed.addField("**" + devi.getTranslation(command.getLanguage(), 501) + "**", Emote.ONLINE + " Looking to Play", true);
                break;
        }

        embed.addField("**" + devi.getTranslation(command.getLanguage(), 502) + "**", String.valueOf(userLevel.getInt("player_level")), true);

        if (bans.getBoolean("VACBanned")) {
            embed.addField("**VAC Ban**", Emote.SUCCESS + " " +
                    bans.getInt("NumberOfVACBans") +
                    " ban(s) found. \n" +
                    "Issued " + bans.getInt("DaysSinceLastBan") + " days ago.", true);
        } else
            embed.addField("**" + devi.getTranslation(command.getLanguage(), 503) + "**", Emote.ERROR + " " + devi.getTranslation(command.getLanguage(), 505), true);

        if (bans.getBoolean("CommunityBanned")) {
            embed.addField("**" + devi.getTranslation(command.getLanguage(), 504) + "**", Emote.SUCCESS + " " + devi.getTranslation(command.getLanguage(), 506), true);
        } else
            embed.addField("**" + devi.getTranslation(command.getLanguage(), 504) + "**", Emote.ERROR + " " + devi.getTranslation(command.getLanguage(), 505), true);

        if (bans.getString("EconomyBan").contains("none")) {
            embed.addField("**" + devi.getTranslation(command.getLanguage(), 507) + "**", Emote.ERROR + " " + devi.getTranslation(command.getLanguage(), 505), true);
        } else if (bans.getString("EconomyBan").contains("probation")) {
            embed.addField("**" + devi.getTranslation(command.getLanguage(), 507) + "**", Emote.INFO + " " + devi.getTranslation(command.getLanguage(), 509), true);
        } else if (bans.getString("EconomyBan").contains("banned")) {
            embed.addField("**" + devi.getTranslation(command.getLanguage(), 507) + "**", Emote.SUCCESS + " " + devi.getTranslation(command.getLanguage(), 508), true);
        }

        sender.reply(embed.build());
    }
}
