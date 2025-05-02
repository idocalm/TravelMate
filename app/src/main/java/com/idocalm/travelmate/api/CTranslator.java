package com.idocalm.travelmate.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CTranslator {

    private static final String API_URL = "https://v6.exchangerate-api.com/v6/42346474fcabda4d6c2db244/latest/"; // Example base URL
    private static final String TAG = "CTranslator";

    public interface TranslationCallback {
        void onSuccess(double result);
        void onFailure(Exception e);
    }

    /**
     * Translates a given amount from one currency to another using OkHttp.
     *
     * @param amount     The amount of money to convert
     * @param fromCode   The source currency code (e.g., "USD")
     * @param toCode     The target currency code (e.g., "EUR")
     * @param callback   Callback to handle success/failure
     */
    public static void translate(int amount, String fromCode, String toCode, TranslationCallback callback) {
        OkHttpClient client = new OkHttpClient();

        String url = API_URL + fromCode; // e.g., https://api.exchangerate-api.com/v4/latest/USD

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "HTTP request failed", e);
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure(new IOException("Unexpected code " + response));
                    return;
                }

                String responseData = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseData);
                    JSONObject rates = json.getJSONObject("conversion_rates");

                    if (rates.has(toCode)) {
                        double rate = rates.getDouble(toCode);
                        double convertedAmount = amount * rate;
                        callback.onSuccess(convertedAmount);
                    } else {
                        callback.onFailure(new IllegalArgumentException("Invalid currency code: " + toCode));
                    }

                } catch (JSONException e) {
                    callback.onFailure(e);
                }
            }
        });
    }
}
