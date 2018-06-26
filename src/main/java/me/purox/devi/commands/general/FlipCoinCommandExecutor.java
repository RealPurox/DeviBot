package me.purox.devi.commands.general;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.Permission;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FlipCoinCommandExecutor implements CommandExecutor {


    private Devi devi;
    public FlipCoinCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {

        Random random = new Random();

        int result = random.nextInt(2);
        if(result == 0){
            sender.reply(devi.getTranslation(command.getLanguage(), 402));
            }else{
            sender.reply(devi.getTranslation(command.getLanguage(), 403));
        }
        }



    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 401;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("coinflip");
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
