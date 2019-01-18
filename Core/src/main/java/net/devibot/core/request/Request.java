package net.devibot.core.request;

import net.devibot.core.utils.JavaUtils;
import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

public class Request {

    public class Response {

        private final String body;
        private final Headers headers;
        private final int status;

        public Response(String body, Headers headers, int status) {
            this.body = body;
            this.headers = headers;
            this.status = status;
        }

        public String getBody() {
            return body;
        }

        public JSONObject getBodyJSON() {
            return new JSONObject(body);
        }

        public Headers getHeaders() {
            return headers;
        }

        public int getStatus() {
            return status;
        }
    }

    public class JSONResponse {

        private final JSONObject body;
        private final Headers headers;
        private final int status;

        public JSONResponse(JSONObject body, Headers headers, int status) {
            this.body = body;
            this.headers = headers;
            this.status = status;
        }

        public JSONObject getBody() {
            return body;
        }

        public Headers getHeaders() {
            return headers;
        }

        public int getStatus() {
            return status;
        }
    }

    public enum Type {
        GET, POST
    }

    private final Logger logger = LoggerFactory.getLogger(Request.class);

    private HashMap<String, String> headers;
    private JSONObject body;
    private String stringBody;
    private String url;
    private Request.Type requestType;
    private OkHttpClient okHttpClient;
    private ScheduledExecutorService threadPool;

    Request(OkHttpClient okHttpClient, ScheduledExecutorService threadPool, String url, HashMap<String, String> headers, Type requestType, JSONObject body, String stringBody) {
        JavaUtils.notNull(url, "URL");
        JavaUtils.notNull(requestType, "RequestType");
        JavaUtils.notNull(okHttpClient, "OkHttpClient");
        this.headers = headers;
        this.body = body;
        this.stringBody = stringBody;
        this.url = url;
        this.requestType = requestType;
        this.okHttpClient = okHttpClient;
        this.threadPool = threadPool;
    }

    public void execute(Consumer<? super Response> success) {
        execute(success, null);
    }

    public void execute(Consumer<? super Response> success, Consumer<? super Throwable> failure) {
        threadPool.submit(() -> {
            try {
                okhttp3.Response response = getResponse();

                if (response.body() != null) {
                    success.accept(new Response(response.body().string(), response.headers(), response.code()));
                } else throw new Exception("Response body null");
            } catch (Exception e) {
                if (failure != null)
                    failure.accept(e);
                else
                    logger.error("", e);
            }
        });
    }

    @Nullable
    public Response executeSync() {
        try {
            okhttp3.Response response = getResponse();

            if (response.body() != null) {
                return new Response(response.body().string(), response.headers(), response.code());
            } else throw new Exception("Response body null");
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
    }

    private okhttp3.Response getResponse() throws IOException {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();

        if (requestType == Type.GET)
            builder.get();
        else if (requestType == Type.POST)
            builder.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), this.stringBody != null ? stringBody : body.toString()));

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.addHeader(header.getKey(), header.getValue());
            }
        }

        builder.url(this.url);

        okhttp3.Request request = builder.build();

        return okHttpClient.newCall(request).execute();
    }
}
