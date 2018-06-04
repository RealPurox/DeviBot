package me.purox.devi.request;

import okhttp3.OkHttpClient;
import org.json.JSONObject;

import java.util.HashMap;

public class RequestBuilder {

    private JSONObject body = null;
    private HashMap<String, String> headers = null;
    private String url = null;
    private Request.RequestType requestType = null;
    private OkHttpClient client;

    public RequestBuilder(OkHttpClient client) {
        this.client = client;
    }

    public RequestBuilder setURL(String url) {
        this.url = url;
        return this;
    }

    public RequestBuilder setRequestType(Request.RequestType requestType) {
        this.requestType = requestType;
        return this;
    }

    public RequestBuilder appendBody(String key, int value) {
        if (this.body == null) this.body = new JSONObject();
        this.body.put(key, value);
        return this;
    }

    public RequestBuilder appendBody(String key, long value) {
        if (this.body == null) this.body = new JSONObject();
        this.body.put(key, value);
        return this;
    }

    public RequestBuilder appendBody(String key, double value) {
        if (this.body == null) this.body = new JSONObject();
        this.body.put(key, value);
        return this;
    }

    public RequestBuilder appendBody(String key, Object value) {
        if (this.body == null) this.body = new JSONObject();
        this.body.put(key, value);
        return this;
    }

    public RequestBuilder addHeader(String key, String value) {
        if (this.headers == null) this.headers = new HashMap<>();
        headers.put(key, value);
        return this;
    }

    public Request build() {
        return new Request(client, url, body, headers, requestType);
    }
}
