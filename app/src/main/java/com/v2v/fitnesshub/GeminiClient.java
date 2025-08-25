package com.v2v.fitnesshub;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiClient {

    public interface PlanCallback {
        void onSuccess(String planText);
        void onError(String message);
    }

    private static final String TAG = "GeminiClient";
    private static final String MODEL = "gemini-1.5-flash";
    private static final String ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/"
                    + MODEL + ":generateContent?key=";

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();

    public static void generateDietPlan(String prompt, PlanCallback cb) {
        String apiKey = BuildConfig.GEMINI_API_KEY;

        if (apiKey == null || apiKey.trim().isEmpty()) {
            postMain(() -> cb.onError("❌ Missing Gemini API key in local.properties"));
            return;
        }

        // Build JSON body
        JsonObject textPart = new JsonObject();
        textPart.addProperty("text", prompt);

        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();
        parts.add(textPart);
        content.add("parts", parts);

        JsonObject root = new JsonObject();
        JsonArray contents = new JsonArray();
        contents.add(content);
        root.add("contents", contents);

        String jsonBody = root.toString();
        Log.d(TAG, "📤 Sending request: " + jsonBody);

        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(ENDPOINT + apiKey)
                .post(body)
                .build();

        Log.d(TAG, "🌍 API URL: " + request.url());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "❌ Network error", e);
                postMain(() -> cb.onError("🌐 Network error: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body() != null ? response.body().string() : "";
                Log.d(TAG, "📥 Raw response: " + res);

                if (!response.isSuccessful()) {
                    Log.e(TAG, "⚠️ API error: Code " + response.code() + " Body: " + res);
                    postMain(() -> cb.onError("⚠️ API error: " + response.code() + "\n" + res));
                    return;
                }

                String plan = extractText(res);
                if (plan == null || plan.trim().isEmpty()) {
                    Log.w(TAG, "⚠️ No content parsed from response");
                    plan = "No content returned. Raw response:\n" + res;
                } else {
                    Log.d(TAG, "✅ Parsed plan: " + plan);
                }

                final String finalPlan = plan;
                postMain(() -> cb.onSuccess(finalPlan));
            }
        });
    }

    // Parse response
    private static String extractText(String json) {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            if (!root.has("candidates")) {
                Log.w(TAG, "⚠️ No candidates in response");
                return null;
            }
            JsonArray cands = root.getAsJsonArray("candidates");
            if (cands.size() == 0) return null;
            JsonObject first = cands.get(0).getAsJsonObject();

            JsonObject content = first.getAsJsonObject("content");
            if (content == null || !content.has("parts")) {
                Log.w(TAG, "⚠️ No parts found");
                return null;
            }
            JsonArray parts = content.getAsJsonArray("parts");
            if (parts.size() == 0) return null;

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < parts.size(); i++) {
                JsonObject p = parts.get(i).getAsJsonObject();
                if (p.has("text")) {
                    sb.append(p.get("text").getAsString());
                }
            }
            return sb.toString();
        } catch (Exception e) {
            Log.e(TAG, "❌ Parse error", e);
            return null;
        }
    }

    private static void postMain(Runnable r) {
        new Handler(Looper.getMainLooper()).post(r);
    }
}

