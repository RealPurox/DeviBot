package net.devibot.core.entities;

import com.google.gson.annotations.SerializedName;
import net.devibot.core.entities.automod.AutoMod;

public class DeviGuild {

    private String id;

    public DeviGuild(String id) {
        this.id = id;
    }

    public DeviGuild(net.devibot.grpc.entities.DeviGuild entity) {
        this.setId(entity.getId());
        this.setPrefix(entity.getPrefix());
        this.setLanguage(entity.getLanguage());
        this.setAutoMod(new AutoMod(entity.getAutoMod()));
    }

    private String prefix = "!";
    private String language = "ENGLISH";

    @SerializedName("auto_mod")
    private AutoMod autoMod = new AutoMod();

    /*
     * ============ METHODS ============
     */

    public net.devibot.grpc.entities.DeviGuild toGrpc() {
        return net.devibot.grpc.entities.DeviGuild.newBuilder()
                .setId(this.id)
                .setPrefix(this.prefix)
                .setLanguage(this.language)
                .setAutoMod(this.autoMod.toGrpc())
                .build();
    }


    /*
     * ============ GETTER ============
     */

    public String getId() {
        return id;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getLanguage() {
        return language;
    }

    public AutoMod getAutoMod() {
        return autoMod;
    }

    /*
     * ============ SETTER ============
     */

    public void setId(String id) {
        this.id = id;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setAutoMod(AutoMod autoMod) {
        this.autoMod = autoMod;
    }
}
