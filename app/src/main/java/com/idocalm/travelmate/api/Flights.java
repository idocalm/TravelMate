package com.idocalm.travelmate.api;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.idocalm.travelmate.models.Airport;
import com.idocalm.travelmate.models.Flight;
import com.idocalm.travelmate.models.FlightResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.OkHttpClient;
import okhttp3.Response;

public class Flights {

    public Flights() {
    }

    public static ArrayList<Airport> fetchAirports(Activity ctx, String country) {
        ArrayList<Airport> airports = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://live-flight-ticket-api.p.rapidapi.com/api/v1/flight/search/airport?name=" + country)
                .get()
                .addHeader("x-rapidapi-host", "live-flight-ticket-api.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "64381c16bbmshac2bfd18e22e798p1dd7dfjsn32dbec546a48")
                .build();

        try {
            Response res = client.newCall(request).execute();

            JSONObject response = new JSONObject(res.body().string());

            JSONArray jsonArray = response.getJSONArray("response");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Airport airport = new Airport(
                        jsonObject.getString("iata"),
                        jsonObject.getString("city"),
                        jsonObject.getString("name")
                );
                airports.add(airport);
            }
        } catch (IOException | JSONException e) {
            Log.e("Flights", "Error fetching airports: " + e.getMessage());
        }

        if (airports.isEmpty()) {
            ctx.runOnUiThread(() ->
                    Toast.makeText(ctx, "Error fetching airports", Toast.LENGTH_SHORT).show()
            );
            return null;
        }

        return airports;
    }

    public static void fetchFlights(Activity ctx, String from, String to, int amount, Date departureDate, FlightCallback callback) {
        new Thread(() -> {
            ArrayList<Flight> flights = new ArrayList<>();
            ArrayList<Airport> departure = fetchAirports(ctx, from);
            ArrayList<Airport> arrival = fetchAirports(ctx, to);

            if (departure == null || arrival == null) {
                ctx.runOnUiThread(() -> Toast.makeText(ctx, "Error fetching airports", Toast.LENGTH_SHORT).show());
                return;
            }

            AtomicReference<String> departureId = new AtomicReference<>();
            AtomicReference<String> arrivalId = new AtomicReference<>();

            ctx.runOnUiThread(() -> {
                if (departure.size() > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setTitle("Select Departure Airport");
                    String[] airportNames = new String[departure.size()];
                    for (int i = 0; i < departure.size(); i++) {
                        airportNames[i] = departure.get(i).getName();
                    }
                    builder.setItems(airportNames, (dialog, which) -> {
                        departureId.set(departure.get(which).getId());
                        proceedToArrivalSelection(ctx, arrival, arrivalId, amount, departureDate, departureId.get(), flights, callback);
                    });
                    builder.show();
                } else {
                    departureId.set(departure.get(0).getId());
                    proceedToArrivalSelection(ctx, arrival, arrivalId, amount, departureDate, departureId.get(), flights, callback);
                }
            });
        }).start();
    }

    private static void proceedToArrivalSelection(Activity ctx, ArrayList<Airport> arrival, AtomicReference<String> arrivalId,
                                                  int amount, Date departureDate, String departureId,
                                                  ArrayList<Flight> flights, FlightCallback callback) {

        if (arrival.size() > 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Select Arrival Airport");
            String[] airportNames = new String[arrival.size()];
            for (int i = 0; i < arrival.size(); i++) {
                airportNames[i] = arrival.get(i).getName();
            }
            builder.setItems(airportNames, (dialog, which) -> {
                arrivalId.set(arrival.get(which).getId());
                fetchFlightData(ctx, amount, departureDate, departureId, arrivalId.get(), flights, callback);
            });
            builder.show();
        } else {
            arrivalId.set(arrival.get(0).getId());
            fetchFlightData(ctx, amount, departureDate, departureId, arrivalId.get(), flights, callback);
        }
    }

    private static void fetchFlightData(Activity ctx, int amount, Date departureDate,
                                        String departureId, String arrivalId,
                                        ArrayList<Flight> flights, FlightCallback callback) {

        String dep = (departureDate.getYear() + 1900) + "-" + (departureDate.getMonth() + 1) + "-" + departureDate.getDate();
        String url = "https://live-flight-ticket-api.p.rapidapi.com/api/v1/flight/search?class=Economy&child6To12Count=0&child=0&infant=0&depart=" + dep;
        url += "&origin=" + departureId;
        url += "&destination=" + arrivalId;
        url += "&adult=" + amount;
        url += "&tripType=OneWay";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-host", "live-flight-ticket-api.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "64381c16bbmshac2bfd18e22e798p1dd7dfjsn32dbec546a48")
                .build();

        new Thread(() -> {
            try {
                Response res = client.newCall(request).execute();
                if (!res.isSuccessful()) {
                    throw new IOException("Unexpected code " + res);
                }
                String rawJson = res.body().string();

                // Async parsing with currency translation support
                FlightResponse.parseFlights(rawJson, new FlightResponse.FlightParseCallback() {
                    @Override
                    public void onParsed(ArrayList<Flight> flights) {
                        ctx.runOnUiThread(() -> callback.onFlightsFetched(flights));
                    }
                });

            } catch (IOException e) {
                Log.e("Flights", "Error fetching flights: " + e.getMessage());
                ctx.runOnUiThread(() -> callback.onError(e));
            }
        }).start();

    }
}

