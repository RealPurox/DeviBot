package me.purox.devi.request;

import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Request {

    public enum RequestType {
        GET, POST
    }

    private String url;
    private JSONObject body;
    private HashMap<String, String> headers;
    private RequestType requestType;
    private OkHttpClient client;

    Request(OkHttpClient client, String url, JSONObject body, HashMap<String, String> headers, RequestType requestType) {
        notNull(url, "URL");
        notNull(requestType, "RequestType");
        notNull(client, "OkHttpClient");
        this.url = url;
        this.body = body;
        this.headers = headers;
        this.requestType = requestType;
        this.client = client;
    }

    public void asString(Consumer<? super StringResponse> success) {
        asString(success, null);
    }

    public void asString(Consumer<? super StringResponse> success, Consumer<? super Exception> failure) {
        new Thread(() -> {
            try {
                okhttp3.Request.Builder builder = new okhttp3.Request.Builder();

                if (requestType == RequestType.GET)
                    builder.get();
                else if (requestType == RequestType.POST)
                    builder.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body.toString()));

                if (headers != null) {
                    for (Map.Entry<String, String> header : headers.entrySet()) {
                        builder.addHeader(header.getKey(), header.getValue());
                    }
                }

                builder.url(url);

                okhttp3.Request request = builder.build();

                Response response = client.newCall(request).execute();
                assert response.body() != null : "Response body is null";
                success.accept(new StringResponse(response.body().string(), response.headers(), response.code()));
            } catch (IOException | NullPointerException e) {
                if (failure != null) {
                    failure.accept(e);
                } else {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public StringResponse asStringSync() {
        try {
            okhttp3.Request.Builder builder = new okhttp3.Request.Builder();

            if (requestType == RequestType.GET)
                builder.get();
            else if (requestType == RequestType.POST)
                builder.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body.toString()));

            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    builder.addHeader(header.getKey(), header.getValue());
                }
            }

            builder.url(url);

            okhttp3.Request request = builder.build();

            Response response;
            try {
                response = client.newCall(request).execute();
            } catch (ConnectException e) {
                response = null;
            }

            if (response == null) return new StringResponse("error", null, 500);
            assert response.body() != null : "Response body is null";
            return new StringResponse(response.body().string(), response.headers(), response.code());
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void asJSON(Consumer<? super JSONResponse> success) {
        asJSON(success, null);
    }

    public void asJSON(Consumer<? super JSONResponse> success, Consumer<? super Exception> failure) {
        new Thread(() -> {
            try {
                okhttp3.Request.Builder builder = new okhttp3.Request.Builder();

                if (requestType == RequestType.GET)
                    builder.get();
                else if (requestType == RequestType.POST)
                    builder.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body.toString()));

                if (headers != null) {
                    for (Map.Entry<String, String> header : headers.entrySet()) {
                        builder.addHeader(header.getKey(), header.getValue());
                    }
                }

                builder.url(url);

                okhttp3.Request request = builder.build();

                Response response = client.newCall(request).execute();
                assert response.body() != null : "Response body is null";
                success.accept(new JSONResponse(new JSONObject(response.body().string()), response.headers(), response.code()));
            } catch (IOException | NullPointerException | JSONException e) {
                if (failure != null) {
                    failure.accept(e);
                } else {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public JSONResponse asJSONSync() {
        try {
            okhttp3.Request.Builder builder = new okhttp3.Request.Builder();

            if (requestType == RequestType.GET)
                builder.get();
            else if (requestType == RequestType.POST)
                builder.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body.toString()));

            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    builder.addHeader(header.getKey(), header.getValue());
                }
            }

            builder.url(url);

            okhttp3.Request request = builder.build();

            Response response = client.newCall(request).execute();
            assert response.body() != null : "Response body is null";
            return new JSONResponse(new JSONObject(response.body().string()), response.headers(), response.code());
        } catch (IOException | NullPointerException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public class StringResponse {
        private final String body;
        private final Headers headers;
        private final int status;

        StringResponse(String body, Headers headers, int status) {
            this.body = body;
            this.headers = headers;
            this.status = status;
        }

        public Headers getHeaders() {
            return headers;
        }

        public int getStatus() {
            return status;
        }

        public String getBody() {
            return body;
        }
    }

    public class JSONResponse {
        private final JSONObject body;
        private final Headers headers;
        private final int status;

        JSONResponse(JSONObject body, Headers headers, int status) {
            this.body = body;
            this.headers = headers;
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        public Headers getHeaders() {
            return headers;
        }

        public JSONObject getBody() {
            return body;
        }
    }

    private void notNull(Object argument, String name) {
        if (argument == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
    }
}
