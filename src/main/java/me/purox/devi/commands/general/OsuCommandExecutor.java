package me.purox.devi.commands.general;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.waiter.WaitingResponse;
import me.purox.devi.core.waiter.WaitingResponseBuilder;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;

import java.util.List;

public class OsuCommandExecutor implements CommandExecutor {

    private Devi devi;

    public OsuCommandExecutor(Devi devi){
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        // prepare myself that this is going to be rly intense. honestly this is going to be so messy but idgaf
        WaitingResponse searchProfileStandard = new WaitingResponseBuilder(devi, command)
                .setReplyText("")
                .setWaiterType(WaitingResponseBuilder.WaiterType.CUSTOM)
                .setExpectedInputText("Please enter the name of the user you want to view.")
                .setTryAgainAfterCustomCheckFail(true)
                .withCustomCheck((response) -> {
                    String baseUrl = "https://osu.ppy.sh/api/get_user?k=" + devi.getSettings().getOsuApiKey() + "&u=";
                    String modeStandrad = "&m=0";
                    String search = response.getMessage().getContentRaw();
                    try {
                        Request.JSONResponse res = new RequestBuilder(devi.getOkHttpClient()).setURL(baseUrl + search + modeStandrad)
                                .setRequestType(Request.RequestType.GET).build()
                                .asJSONSync();
                        JSONArray body = res.getBody().names();

                        if (res.getStatus() == 429 || body.length() <= 0) {
                            sender.reply("Couldn't find " + search);
                            return null;
                        }
                        JSONArray user = new JSONArray(body);

                        System.out.println(user.getJSONObject(0).getString("country"));
                        sender.reply("Country from osu! api: " + user.getJSONObject(0).getString("country"));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .build();

        WaitingResponse searchBeatmap = new WaitingResponseBuilder(devi, command)
                .setReplyText("")
                .setWaiterType(WaitingResponseBuilder.WaiterType.CUSTOM)
                .setExpectedInputText("Please enter a link of the beatmap you want to view.")
                .setTryAgainAfterCustomCheckFail(true)
                .build();

        WaitingResponse searchProfileSelectMode = new WaitingResponseBuilder(devi, command)
                .setReplyText("")
                .setWaiterType(WaitingResponseBuilder.WaiterType.SELECTOR)
                .setExpectedInputText("Select an osu! mode.")

                .addSelectorOption("osu! Standard", searchProfileStandard)
                .addSelectorOption("osu! Taiko", searchProfileStandard)
                .addSelectorOption("osu! Catch The Beat",searchProfileStandard )
                .addSelectorOption("osu! Mania", searchProfileStandard)
                .build();


        new WaitingResponseBuilder(devi, command)
                .setWaiterType(WaitingResponseBuilder.WaiterType.SELECTOR)
                .setExpectedInputText("What do you want to do?")

                .addSelectorOption("Lookup a user's osu! profile.", searchProfileSelectMode)
                .addSelectorOption("Lookup a osu! beatmap.", searchBeatmap)
                .setInfoText("Please choose one of the two alternatives.")
                .build().handle();
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
        return ModuleType.GAME_COMMANDS;
    }
}
