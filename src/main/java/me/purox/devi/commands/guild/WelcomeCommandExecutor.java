package me.purox.devi.commands.guild;

import me.purox.devi.commands.guild.handler.*;
import me.purox.devi.commands.handler.*;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class    WelcomeCommandExecutor implements CommandExecutor {

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
        String builder = Emote.INFO.get() + " | You're currently editing the Welcome module\n\n" +
                "```python\n" +
                "Reply with one of the options listed below to edit your Welcome module settings\n\n" +
                " <== Welcome Module ==>\n\n" +
                " '1' => Enable or disable welcome messages\n" +
                " '2' => Change the welcome message channel\n" +
                " '3' => Edit the join message\n" +
                " '4' => Edit the leave message\n\n" +
                " <== Auto-Role ==>\n\n" +
                " '5' => Enable or disable Auto-Role\n" +
                " '6' => Change the Auto-Role role\n" +
                "```\nYou can cancel editing your Welcome module settings by typing `cancel`\n";

        sender.reply(builder);
        startWaiter(1, command, sender);
    }

    private void startWaiter(int attempt, Command command, CommandSender sender) {
        int nextAttempt = attempt += 1;
        MessageReceivedEvent event = command.getEvent();
        devi.getResponseWaiter().waitForResponse(event.getGuild(),
                evt -> devi.getResponseWaiter().checkUser(evt, event.getMessageId(), event.getAuthor().getId(), event.getChannel().getId()),
                response -> {
                    if (response.getMessage().getContentRaw().toLowerCase().startsWith("cancel")) {
                        sender.reply(Emote.ERROR.get() + " | Welcome module settings selection has been cancelled");
                        return;
                    }

                    if (nextAttempt >= 4) {
                        sender.reply(Emote.ERROR.get() + " | You've failed to enter a valid number 3 times in a row. Welcome module settings selection has been cancelled.");
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
                        case 1:
                            String builder = Emote.INFO.get() + " | You're currently editing the Welcome module -> Enable or disable the Welcome module\n\n" +
                                    "```python\n" +
                                    "Reply with one of the options listed below to edit your Welcome module settings\n\n" +
                                    " '1' => Enable welcome messages\n" +
                                    " '2' => Disable welcome messages\n" +
                                    "```\nYou can cancel editing your Welcome module settings by typing `cancel`\n\n";
                            sender.reply(builder);
                            welcomeEnabledHandler.startWaiter(1, command, sender);
                            break;
                        case 2:
                            welcomeChannelHandler.startWaiter(1, command, sender);
                            break;
                        case 3:
                            welcomeJoinMessageHandler.startWaiter(1, command, sender);
                            break;
                        case 4:
                            welcomeLeaveMessageHandler.startWaiter(1, command, sender);
                            break;
                        case 5:
                            welcomeAutoModEnabledHandler.startWaiter(1, command, sender);
                            break;
                        case 6:
                            welcomeAutoModRoleHandler.startWaiter(1, command, sender);
                            break;
                        default:
                            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 265));
                            startWaiter(nextAttempt, command, sender);
                            break;
                    }
                },
                1, TimeUnit.MINUTES, () -> sender.reply(Emote.ERROR.get() + " | You took to long to respond to my message. Welcome module settings selection has been cancelled."));
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
