package me.purox.devi.commands.guild;

import me.purox.devi.commands.guild.handler.*;
import me.purox.devi.commands.handler.*;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class WelcomeCommandExecutor implements CommandExecutor {

    private Devi devi;

    private WelcomeEnabledHandler welcomeEnabledHandler;
    private WelcomeChannelHandler welcomeChannelHandler;
    private WelcomeJoinMessageHandler welcomeJoinMessageHandler;
    private WelcomeLeaveMessageHandler welcomeLeaveMessageHandler;
    private WelcomeAutoModEnabledHandler welcomeAutoModEnabledHandler;
    private WelcomeAutoModRoleHandler welcomeAutoModRoleHandler;

    public WelcomeCommandExecutor(Devi devi) {
        this.devi = devi;
        this.welcomeEnabledHandler = new WelcomeEnabledHandler(devi);
        this.welcomeChannelHandler = new WelcomeChannelHandler(devi);
        this.welcomeJoinMessageHandler = new WelcomeJoinMessageHandler(devi);
        this.welcomeLeaveMessageHandler = new WelcomeLeaveMessageHandler(devi);
        this.welcomeAutoModEnabledHandler = new WelcomeAutoModEnabledHandler(devi);
        this.welcomeAutoModRoleHandler = new WelcomeAutoModRoleHandler(devi);
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        String builder = ":information_source: | You're currently editing the Welcome module\n\n" +
                "```python\n" +
                "Reply with one of the options listed below to edit your Welcome module settings\n\n" +
                " <== Welcome Module ==>\n" +
                " '1' => Enable or disable the Welcome module\n" +
                " '2' => Change the Welcome message channel\n" +
                " '3' => Edit the join message\n" +
                " '4' => Edit the leave message\n\n" +
                " <== Auto-Mode ==>\n" +
                " '5' => Enable or disable Auto-Role\n" +
                " '6' => Change the Auto-Role role\n" +
                "```\nYou can cancel editing your Welcome settings by typing `cancel`\n";

        sender.reply(builder);
    }

    private void startWaiter(int attempt, Command command, CommandSender sender) {
        int nextAttempt = attempt += 1;
        MessageReceivedEvent event = command.getEvent();
        devi.getResponseWaiter().waitForResponse(event.getGuild(),
                evt -> devi.getResponseWaiter().checkUser(evt, event.getMessageId(), event.getAuthor().getId(), event.getChannel().getId()),
                response -> {
                    if (response.getMessage().getContentRaw().toLowerCase().startsWith("cancel")) {
                        sender.reply(DeviEmote.ERROR.get() + " | Welcome module settings selection has been cancelled");
                        return;
                    }

                    if (nextAttempt >= 4) {
                        sender.reply(DeviEmote.ERROR.get() + " | You've failed to enter a valid number 3 times in a row. Welcome module settings selection has been cancelled.");
                        return;
                    }

                    String input = response.getMessage().getContentRaw().split(" ")[0];

                    int entered;
                    try {
                        entered = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        entered = -1;
                    }

                    switch (entered) {
                        //display
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                        case 5:
                            break;
                        case 6:
                            break;
                        default:
                            sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 265));
                            startWaiter(nextAttempt, command, sender);
                            break;
                    }
                },
                1, TimeUnit.MINUTES, () -> sender.reply(DeviEmote.ERROR.get() + " | You took to long to respond to my message. Welcome module settings selection has been cancelled."));
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 376;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.WELCOME;
    }
}
