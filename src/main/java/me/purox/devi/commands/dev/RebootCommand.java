package me.purox.devi.commands.dev;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.waiter.WaitingResponse;
import me.purox.devi.core.waiter.WaitingResponseBuilder;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RebootCommand implements CommandExecutor {

    private Devi devi;

    public RebootCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        WaitingResponseBuilder builder = new WaitingResponseBuilder(devi, command)
                .setExpectedInputText("When do you want to restart Devi?")
                .setWaiterType(WaitingResponseBuilder.WaiterType.SELECTOR);
        builder.addSelectorOption("1 min (if urgent)", createWaitingResponse(1, command));
        builder.addSelectorOption("5 minutes", createWaitingResponse(5, command));
        builder.addSelectorOption("15 minutes", createWaitingResponse(15, command));
        builder.addSelectorOption("30 minutes", createWaitingResponse(30, command));
        builder.addSelectorOption("1 hour", createWaitingResponse(60, command));
        builder.setTryAgainAfterCustomCheckFail(false);
        builder.setCustomCheckFailureText("Lmao you failed");

        builder.build().handle();
    }

    private WaitingResponse createWaitingResponse(int minutes, Command command) {
        WaitingResponseBuilder responseBuilder = new WaitingResponseBuilder(devi, command);
        responseBuilder.setWaiterType(WaitingResponseBuilder.WaiterType.CUSTOM);

        responseBuilder.withCustomVoid(object ->  {
            devi.reboot(minutes, command.getEvent().getChannel());
            MessageUtils.sendMessageAsync(command.getEvent().getChannel(), "Devi will restart in " + minutes + "!");
        });
        return responseBuilder.build();
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
        return ModuleType.DEV;
    }
}
