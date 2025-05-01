package com.idocalm.travelmate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * The type Flight response.
 */
public class FlightResponse {

    /**
     * Parse flights array list.
     *
     * @param rawJson the raw json
     * @return the array list
     */
    public static ArrayList<Flight> parseFlights(String rawJson) {
        ArrayList<Flight> flightsList = new ArrayList<>();

        Log.d("FlightResponse", "Parsing flight response: " + rawJson);
        try {
            JSONObject root = new JSONObject(rawJson);
            Log.d("FlightResponse", "Parsed JSON: " + root.toString());
            JSONObject response = root.getJSONObject("response");
            JSONArray flights = response.getJSONArray("flights");

            for (int i = 0; i < flights.length(); i++) {
                JSONObject flightJson = flights.getJSONObject(i);
                JSONObject departDateTime = flightJson.getJSONObject("departStartDate");
                JSONArray segments = flightJson.getJSONArray("segments");

                Flight flight = new Flight();
                flight.dealType = flightJson.optString("deal");
                flight.price = flightJson.optInt("price");
                flight.currency = flightJson.optString("currency");
                flight.totalDuration = flightJson.optString("totalDuration");
                flight.departureDate = departDateTime.optString("date");
                flight.departureTime = departDateTime.optString("time");
                flight.refundable = flightJson.optBoolean("refundable");
                flight.isRefundable = flightJson.optString("isRefundable");
                flight.airlineName = flightJson.getJSONArray("flight")
                        .getJSONObject(0)
                        .getJSONObject("airlines")
                        .optString("full");
                flight.imageUrl = flightJson.getJSONArray("flight")
                        .getJSONObject(0)
                        .optString("logo");

                flight.segments = new ArrayList<>();
                JSONArray segmentArray = segments.getJSONObject(0).getJSONArray("segment");
                for (int j = 0; j < segmentArray.length(); j++) {
                    JSONObject seg = segmentArray.getJSONObject(j);

                    Flight.Segment segment = new Flight.Segment();
                    segment.airline = seg.getJSONObject("airlines").optString("short");
                    segment.flightNumber = seg.optString("flightNumber");
                    segment.origin = seg.optString("originCode");
                    segment.destination = seg.optString("destinationCode");
                    segment.departureDate = seg.getJSONObject("departureDateTime").optString("date");
                    segment.departureTime = seg.getJSONObject("departureDateTime").optString("time");
                    segment.arrivalDate = seg.getJSONObject("arrivalDateTime").optString("date");
                    segment.arrivalTime = seg.getJSONObject("arrivalDateTime").optString("time");
                    segment.duration = seg.optString("duration");
                    segment.cabin = seg.optString("cabin");
                    segment.aircraft = seg.optString("aircraft");
                    segment.terminalFrom = seg.optString("originTerminal");
                    segment.terminalTo = seg.optString("destinationTerminal");

                    flight.segments.add(segment);
                }

                flightsList.add(flight);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flightsList;
    }
}
