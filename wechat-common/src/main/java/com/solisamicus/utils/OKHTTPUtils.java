package com.solisamicus.utils;

import com.solisamicus.grace.result.GraceJSONResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
public class OKHTTPUtils {
    private static final OkHttpClient CLIENT = new OkHttpClient();

    public static GraceJSONResult get(String url) {
        try {
            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .build();
            Response response = CLIENT.newCall(request).execute();
            String res = response.body().string();
            return JsonUtils.jsonToPojo(res, GraceJSONResult.class);
        } catch (Exception e) {
            log.error("Get failed:", e);
        }
        return null;
    }
}
