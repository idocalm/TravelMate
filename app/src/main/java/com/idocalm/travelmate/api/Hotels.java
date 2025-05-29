package com.idocalm.travelmate.api;

import android.util.Log;
import android.widget.Toast;

import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.models.Hotel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Hotels {

    public Hotels() {
    }

    public static Hotel[] fetchHotels(String location, int peopleAmount, Date checkIn, Date checkOut) throws IOException, JSONException {

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
            if (locationObject.getString("dest_type").equals("city") ) {
                city = locationObject;
                break;
            }
        }

        if (city == null) {
            return null;
        }

        String cityId = city.getString("dest_id");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String checkOutDate = formatter.format(checkOut);
        String checkInDate = formatter.format(checkIn);
        String currency = Auth.getUser().getCurrencyString();

        Log.d("Hotels", "fetchHotels: " + cityId + " " + checkOutDate + " " + checkInDate + " " + currency);

        request = new Request.Builder()
                .url("https://booking-com.p.rapidapi.com/v2/hotels/search?dest_id=" + cityId + "&order_by=popularity&children_number=1&children_ages=0&checkout_date=" + checkOutDate + "&filter_by_currency="+currency+"&locale=en-gb&dest_type=city&checkin_date="+checkInDate+"&page_number=0&adults_number="+peopleAmount+"&room_number=1&units=metric")
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

        Hotel[] hotelList = new Hotel[hotelsArray.length()];
        for (int i = 0; i < hotelsArray.length(); i++) {
            JSONObject hotel = hotelsArray.getJSONObject(i);
            hotelList[i] = new Hotel(
                    hotel.getInt("id"),
                    hotel.getString("name"),
                    hotel.getString("photoMainUrl"),
                    1000, // TODO
                    checkIn,
                    checkOut
            );
        }
            // Parse the response
        return hotelList;
    }

    public static JSONObject getHotelData(Hotel hotel) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();

        String currency = Auth.getUser().getCurrencyString();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String checkInDate = formatter.format(hotel.getCheckInDate());
        String checkOutDate = formatter.format(hotel.getCheckOutDate());

        String url = "https://booking-com.p.rapidapi.com/v2/hotels/details?locale=en-gb&checkin_date="+checkInDate+"&hotel_id="+hotel.getId()+"&currency="+currency+"&checkout_date="+checkOutDate;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-host", "booking-com.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "64381c16bbmshac2bfd18e22e798p1dd7dfjsn32dbec546a48")
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        return new JSONObject(response.body().string());
    }

}
