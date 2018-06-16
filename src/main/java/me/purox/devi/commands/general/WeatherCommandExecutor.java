package me.purox.devi.commands.general;

import com.github.fedy2.weather.YahooWeatherService;
import com.github.fedy2.weather.data.Channel;
import com.github.fedy2.weather.data.unit.DegreeUnit;
import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;

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
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length == 0) {
            sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "weather <city>`"));
            return;
        }

        String title, wind;

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

            //Global
            title = city.getTitle();
            title = title.substring(16);

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

            EmbedBuilder embedw = new EmbedBuilder();

            embedw.setAuthor(devi.getTranslation(command.getLanguage(), 380, title));
            embedw.setColor(Color.ORANGE);
            embedw.setFooter(devi.getTranslation(command.getLanguage(), 380, title) + " | " + date, null);

            embedw.addField(devi.getTranslation(command.getLanguage(), 381), temp + "/" + tempF, true);
            embedw.addField(devi.getTranslation(command.getLanguage(), 382), wind, true);
            embedw.addField(devi.getTranslation(command.getLanguage(), 383), humidity, true);
            embedw.addField(devi.getTranslation(command.getLanguage(), 384), pressure, true);
            embedw.addField(devi.getTranslation(command.getLanguage(), 385), visibility, true);
            embedw.addField(devi.getTranslation(command.getLanguage(), 386), conditions, true);

            sender.reply(embedw.build());
        } catch (IOException | JAXBException ex) {
            sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 217));
        } catch (IndexOutOfBoundsException ioobe) {
            sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 387));
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
