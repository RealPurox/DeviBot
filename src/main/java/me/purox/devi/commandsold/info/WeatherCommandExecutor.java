package me.purox.devi.commandsold.info;

import com.github.fedy2.weather.YahooWeatherService;
import com.github.fedy2.weather.data.Channel;
import com.github.fedy2.weather.data.unit.DegreeUnit;
import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandExecutor;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import javax.xml.bind.JAXBException;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class WeatherCommandExecutor implements CommandExecutor {

    private Devi devi;

    public WeatherCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, ICommand command, CommandSender sender) {
        if (args.length == 0) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "weather <city>`"));
            return;
        }

        String wind;
        //Wind
        String direction, speed;

        //Atmosphere
        String humidity, pressure, visibility;

        //Condition
        String temp, tempF, conditions, date;

        StringBuilder input = new StringBuilder();
        for (String arg : args) {
            input.append(arg);
        }

        try {

            YahooWeatherService service = new YahooWeatherService();
            YahooWeatherService.LimitDeclaration limit = service.getForecastForLocation(input.toString(), DegreeUnit.CELSIUS);

            List<Channel> list = limit.first(1);
            Channel city = list.get(0);
            if(city != null) {
                //wind
                direction = city.getWind().getDirection() + "°";
                speed = city.getWind().getSpeed() + "km/h";
                wind = devi.getTranslation(command.getLanguage(), 378) + ": " + direction + "\n" + devi.getTranslation(command.getLanguage(), 379) + ": " + speed;

                //Atmosphere
                humidity = city.getAtmosphere().getHumidity() + "%";
                double pRound = city.getAtmosphere().getPressure();
                pressure = String.format("%.0f", pRound) + " psi";
                visibility = city.getAtmosphere().getVisibility() / 100 + " miles";

                //Conditions
                conditions = city.getItem().getCondition().getText();
                temp = city.getItem().getCondition().getTemp() + "°C";
                date = city.getItem().getCondition().getDate().toString();

                //Temp in fahrenheit
                double temper = (double) Math.round((city.getItem().getCondition().getTemp() * 1.8 + 32) * 100) / 100;
                tempF = temper + "°F";

                EmbedBuilder embed = new EmbedBuilder();

                switch (city.getItem().getCondition().getCode()) {

                    case 29: // partly cloudy night
                    case 30: // partly cloudy day
                        embed.setColor(Color.decode("#cccccc"));
                        break;
                    case 27: //mostly cloudy night
                    case 28: //mostly clody day
                    case 24: //windy
                        embed.setColor(Color.decode("#bfbfbf"));
                        break;
                    case 26: // clody
                        embed.setColor(Color.decode("#a6a6a6"));
                        break;
                    case 31: //clear night
                        embed.setColor(Color.cyan);
                        break;
                    case 32: // sunny
                        embed.setColor(Color.yellow);
                        break;
                    case 36: // hot
                        embed.setColor(Color.orange);
                        break;
                    case 2: // rip i forgot all thses :_:
                        embed.setColor(Color.decode("#737373"));
                        break;
                    case 3:
                    case 4:
                        embed.setColor(Color.decode("#404040"));
                        break;
                    case 5:
                        embed.setColor(Color.decode("#ccffff"));
                        break;
                    case 8:
                    case 10:
                        embed.setColor(Color.decode("#00cccc"));
                        break;
                    case 9:
                        embed.setColor(Color.decode("#0080ff"));
                        break;
                    case 11:
                    case 12:
                        embed.setColor(Color.decode("#0066cc"));
                        break;
                    case 13:
                    case 14:
                    case 15:
                    case 41:
                    case 42:
                    case 43:
                        embed.setColor(Color.white);
                        break;
                }


                embed.setAuthor(devi.getTranslation(command.getLanguage(), 380, city.getTitle().substring(17)));
                embed.setFooter(devi.getTranslation(command.getLanguage(), 380, city.getTitle().substring(17)) + " | " + date, null);

                embed.addField(devi.getTranslation(command.getLanguage(), 381), temp + "/" + tempF, true);
                embed.addField(devi.getTranslation(command.getLanguage(), 382), wind, true);
                embed.addField(devi.getTranslation(command.getLanguage(), 383), humidity, true);
                embed.addField(devi.getTranslation(command.getLanguage(), 384), pressure, true);
                embed.addField(devi.getTranslation(command.getLanguage(), 385), visibility, true);
                embed.addField(devi.getTranslation(command.getLanguage(), 386), conditions, true);

                sender.reply(embed.build());
            }
        } catch (IOException | JAXBException ex) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 217) + ".");
        } catch (IndexOutOfBoundsException ioobe) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 387) + ".");
        }
    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 400;
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
        return ModuleType.INFO_COMMANDS;
    }
}
