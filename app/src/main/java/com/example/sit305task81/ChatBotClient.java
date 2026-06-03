package com.example.sit305task81;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ChatBotClient {
    private static final String TAG = "ChatBotClient";

    public String ask(String username, String message) {
        if (BuildConfig.OPENAI_API_KEY == null || BuildConfig.OPENAI_API_KEY.trim().isEmpty()) {
            return fallbackReply(username, message);
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(30000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + BuildConfig.OPENAI_API_KEY.trim());

            JSONObject payload = new JSONObject();
            payload.put("model", BuildConfig.OPENAI_MODEL);
            JSONArray messages = new JSONArray();
            messages.put(new JSONObject()
                    .put("role", "system")
                    .put("content", "You are a friendly, concise chatbot inside an Android assessment app."));
            messages.put(new JSONObject()
                    .put("role", "user")
                    .put("content", username + " says: " + message));
            payload.put("messages", messages);
            payload.put("temperature", 0.7);

            byte[] body = payload.toString().getBytes(StandardCharsets.UTF_8);
            try (OutputStream output = connection.getOutputStream()) {
                output.write(body);
            }

            int status = connection.getResponseCode();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    status >= 200 && status < 300
                            ? connection.getInputStream()
                            : connection.getErrorStream(),
                    StandardCharsets.UTF_8
            ));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            if (status < 200 || status >= 300) {
                Log.e(TAG, "OpenAI error " + status + ": " + response);
                return readableApiError(status, response.toString());
            }

            JSONObject json = new JSONObject(response.toString());
            return json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();
        } catch (Exception exception) {
            Log.e(TAG, "OpenAI request failed", exception);
            return "I had trouble contacting the AI service, but I am still here. Try again in a moment.";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String fallbackReply(String username, String message) {
        String clean = message.trim();
        if (clean.endsWith("?")) {
            return "Good question, " + username + ". Add an OPENAI_API_KEY in local.properties and I can answer with a live LLM.";
        }
        return "Welcome " + username + "! You said: \"" + clean + "\". Add an OPENAI_API_KEY to enable live AI replies.";
    }

    private String readableApiError(int status, String responseBody) {
        try {
            JSONObject json = new JSONObject(responseBody);
            if (json.has("error")) {
                JSONObject error = json.getJSONObject("error");
                String message = error.optString("message", "Unknown OpenAI API error.");
                String type = error.optString("type", "");
                return "OpenAI API error (" + status + "): " + message
                        + (type.isEmpty() ? "" : " [" + type + "]");
            }
        } catch (Exception ignored) {
            Log.w(TAG, "Could not parse OpenAI error body");
        }
        return "OpenAI API error (" + status + "). Check Logcat for details.";
    }
}
6