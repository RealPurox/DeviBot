package net.devibot.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Core {

    public enum Type {
        MAINFRAME, PROVIDER,
    }

    //CONFIG
    public static Config CONFIG;
    //GSON
    public static final Gson GSON = new GsonBuilder().create();
    //Core Type
    public static Type TYPE;

    public static void setup() {
        //load config
        CONFIG = Config.loadConfig();

        try {
            Class.forName("net.devibot.provider.Provider");
            TYPE = Type.PROVIDER;
        } catch (ClassNotFoundException e) {
            TYPE = Type.MAINFRAME;
        }
    }
}
