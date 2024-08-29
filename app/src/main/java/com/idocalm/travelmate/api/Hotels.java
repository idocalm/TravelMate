package com.idocalm.travelmate.api;

import android.util.Log;
import android.widget.Toast;

import com.idocalm.travelmate.models.Hotel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Hotels {

    public Hotels() {
        // Required empty public constructor
    }



    public static Hotel[] fetchHotels(String location) throws IOException, JSONException {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://booking-com.p.rapidapi.com/v1/hotels/locations?locale=en-gb&name=" + location)
                .get()
                .addHeader("x-rapidapi-host", "booking-com.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "64381c16bbmshac2bfd18e22e798p1dd7dfjsn32dbec546a48")
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        JSONArray locations = new JSONArray(response.body().string());
        JSONObject city = null;

        for (int i = 0; i < locations.length(); i++) {
            JSONObject locationObject = locations.getJSONObject(i);
            if (locationObject.getString("dest_type").equals("city")) {
                city = locationObject;
                break;
            }
        }

        if (city == null) {
            throw new IOException("City not found");
        }

        String cityId = city.getString("dest_id");
        String checkOutDate = "2024-10-10";
        String checkInDate = "2024-10-01";
        String currency = "USD";


        request = new Request.Builder()
                .url("https://booking-com.p.rapidapi.com/v2/hotels/search?dest_id=" + cityId + "&order_by=popularity&checkout_date=" + checkOutDate + "&children_number=2&filter_by_currency="+currency+"&locale=en-gb&dest_type=city&checkin_date="+checkInDate+"&children_ages=5%2C0&page_number=0&adults_number=2&room_number=1&units=metric")
                .get()
                .addHeader("X-RapidAPI-Host", "booking-com.p.rapidapi.com")
                .addHeader("X-RapidAPI-Key", "64381c16bbmshac2bfd18e22e798p1dd7dfjsn32dbec546a48")
                .build();

        response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        JSONObject hotels = new JSONObject(response.body().string());
        JSONArray hotelsArray = hotels.getJSONArray("results");

        Hotel[] hotelList = new Hotel[5];
        for (int i = 0; i < 5; i++) {
            JSONObject hotel = hotelsArray.getJSONObject(i);
            hotelList[i] = new Hotel(
                    hotel.getInt("id"),
                    hotel.getString("name"),
                    hotel.getString("photoMainUrl"),
                    hotel.getLong("latitude"),
                    hotel.getLong("longitude"),
                    1000
            );
        }
            // Parse the response
        return hotelList;
    }


}
