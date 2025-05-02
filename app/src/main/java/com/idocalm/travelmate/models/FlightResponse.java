package com.idocalm.travelmate.models;

import android.util.Log;
import android.widget.Toast;

import com.idocalm.travelmate.api.CTranslator;
import com.idocalm.travelmate.auth.Auth;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;



/**
 * The type Flight response.
 */
public class FlightResponse {


    public interface FlightParseCallback {
        void onParsed(ArrayList<Flight> flights);
    }

    /**
     * Parses the flight response from the API.
     *
     * @param rawJson  The raw JSON string from the API response.
     * @param callback The callback to handle the parsed flights.
     */
    public static void parseFlights(String rawJson, FlightParseCallback callback) {
        ArrayList<Flight> flightsList = new ArrayList<>();
        ArrayList<Flight> flightsToConvert = new ArrayList<>();

        Log.d("FlightResponse", "Parsing flight response: " + rawJson);
        try {
            JSONObject root = new JSONObject(rawJson);
            JSONObject response = root.getJSONObject("response");
            JSONArray flights = response.getJSONArray("flights");

            if (flights.length() == 0) {
                callback.onParsed(flightsList); // Return empty list
                return;
            }

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
                        .getJSONObject(0).getJSONObject("airlines").optString("full");
                flight.imageUrl = flightJson.getJSONArray("flight")
                        .getJSONObject(0).optString("logo");

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

                if (!flight.currency.equals(Auth.getUser().getCurrencyString())) {
                    flightsToConvert.add(flight);
                } else {
                    flightsList.add(flight);
                }
            }

            // Handle asynchronous translation if needed
            if (flightsToConvert.isEmpty()) {
                callback.onParsed(flightsList); // No conversion needed
            } else {
                final int[] completed = {0};
                for (Flight flight : flightsToConvert) {
                    CTranslator.translate(flight.price, flight.currency, Auth.getUser().getCurrencyString(), new CTranslator.TranslationCallback() {
                        @Override
                        public void onSuccess(double result) {
                            flight.price = (int) result;
                            flight.currency = Auth.getUser().getCurrencyString();
                            flightsList.add(flight);
                            checkDone();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("FlightResponse", "Translation error: " + e.getMessage());
                            flightsList.add(flight);
                            checkDone();
                        }

                        private void checkDone() {
                            completed[0]++;
                            if (completed[0] == flightsToConvert.size()) {
                                callback.onParsed(flightsList);
                            }
                        }
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            callback.onParsed(flightsList); // Return partial or empty list on error
        }
    }
}
