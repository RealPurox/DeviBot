package net.devibot.core.entities;

import com.google.gson.annotations.SerializedName;

public class Translation {

    @SerializedName("_id")
    private String id;
    private String key;
    private String lang;
    private String text = "none";

    public Translation(String lang, String text) {
        this.lang = lang;
        this.text = text;
    }

    public Translation(net.devibot.grpc.entities.Translation translation) {
        this.id = translation.getId();
        this.key = translation.getKey();
        this.lang = translation.getLang();
        this.text = translation.getText() == null ? "none" : translation.getText();
    }

    public net.devibot.grpc.entities.Translation toGrpc() {
        return net.devibot.grpc.entities.Translation.newBuilder()
                .setId(this.id == null ? "" : this.id)
                .setKey(this.key == null ? "" : this.id)
                .setLang(this.lang)
                .setText(this.text)
                .build();
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getLang() {
        return lang;
    }

    public String getText() {
        return text;
    }
}
