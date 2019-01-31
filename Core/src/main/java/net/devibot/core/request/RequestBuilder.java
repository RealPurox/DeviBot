package net.devibot.core.request;

import net.devibot.core.utils.JavaUtils;
import okhttp3.OkHttpClient;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class RequestBuilder {

    private static final OkHttpClient DEFAULT_HTTP_CLIENT = new OkHttpClient();
    private static final ExecutorService DEFAULT_THREAD_POOL = Executors.newScheduledThreadPool(5);

    private HashMap<String, String> headers = null;

    private JSONObject body = null;
    private String stringBody = null;

    private String url = null;
    private Request.Type requestType = null;

    private OkHttpClient okHttpClient;
    private ExecutorService threadPool;

    public RequestBuilder() {
        this.okHttpClient = DEFAULT_HTTP_CLIENT;
        this.threadPool = DEFAULT_THREAD_POOL;
    }

    public RequestBuilder(OkHttpClient client, ExecutorService threadPool) {
        this.okHttpClient = client;
        this.threadPool = threadPool;
    }

    public RequestBuilder setStringBody(String stringBody) {
        this.stringBody = stringBody;
        return this;
    }

    public RequestBuilder setURL(String url) {
        this.url = url;
        return this;
    }

    public RequestBuilder setRequestType(Request.Type requestType) {
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
        JavaUtils.notNull(url, "URL");
        JavaUtils.notNull(requestType, "RequestType");
        return new Request(okHttpClient, threadPool, url, headers, requestType, body, stringBody);
    }
}
