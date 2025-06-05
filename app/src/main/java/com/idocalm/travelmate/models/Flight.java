package com.idocalm.travelmate.models;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.auth.Auth;

import java.util.ArrayList;
import java.util.HashMap;

public class Flight {
    public String dealType;
    public int price;
    public String currency;
    public String totalDuration;
    public String departureDate;
    public String departureTime;
    public boolean refundable;
    public String isRefundable;
    public String airlineName;
    public String imageUrl;
    public ArrayList<Segment> segments;
    public String dbId;

    public interface FlightsCallback {
        void onFlightsLoaded(ArrayList<Flight> flights);
        void onError(Exception e);
    }


    public static class Segment {
        public String airline;
        public String flightNumber;
        public String origin;
        public String destination;
        public String departureDate;
        public String departureTime;
        public String arrivalDate;
        public String arrivalTime;
        public String duration;
        public String cabin;
        public String aircraft;
        public String terminalFrom;
        public String terminalTo;


        @Override
        public String toString() {
            return "Segment{" +
                    "airline='" + airline + '\'' +
                    ", flightNumber='" + flightNumber + '\'' +
                    ", origin='" + origin + '\'' +
                    ", destination='" + destination + '\'' +
                    ", departureDate='" + departureDate + '\'' +
                    ", departureTime='" + departureTime + '\'' +
                    ", arrivalDate='" + arrivalDate + '\'' +
                    ", arrivalTime='" + arrivalTime + '\'' +
                    ", duration='" + duration + '\'' +
                    ", cabin='" + cabin + '\'' +
                    ", aircraft='" + aircraft + '\'' +
                    ", terminalFrom='" + terminalFrom + '\'' +
                    ", terminalTo='" + terminalTo + '\'' +
                    '}';
        }

        public HashMap<String, Object> toHashMap() {
            HashMap<String, Object> map = new HashMap<>();
            map.put("airline", airline);
            map.put("flightNumber", flightNumber);
            map.put("origin", origin);
            map.put("destination", destination);
            map.put("departureDate", departureDate);
            map.put("departureTime", departureTime);
            map.put("arrivalDate", arrivalDate);
            map.put("arrivalTime", arrivalTime);
            map.put("duration", duration);
            map.put("cabin", cabin);
            map.put("aircraft", aircraft);
            map.put("terminalFrom", terminalFrom);
            map.put("terminalTo", terminalTo);
            return map;
        }

        public static Segment fromHashMap(HashMap<String, Object> map) {
            Segment segment = new Segment();
            segment.airline = (String) map.get("airline");
            segment.flightNumber = (String) map.get("flightNumber");
            segment.origin = (String) map.get("origin");
            segment.destination = (String) map.get("destination");
            segment.departureDate = (String) map.get("departureDate");
            segment.departureTime = (String) map.get("departureTime");
            segment.arrivalDate = (String) map.get("arrivalDate");
            segment.arrivalTime = (String) map.get("arrivalTime");
            segment.duration = (String) map.get("duration");
            segment.cabin = (String) map.get("cabin");
            segment.aircraft = (String) map.get("aircraft");
            segment.terminalFrom = (String) map.get("terminalFrom");
            segment.terminalTo = (String) map.get("terminalTo");
            return segment;
        }
    }

    public Flight() {
    }

    public Flight(String dealType, int price, String currency, String totalDuration,
                 String departureDate, String departureTime, boolean refundable,
                 String isRefundable, String airlineName, String imageUrl,
                 ArrayList<Segment> segments, String dbId) {
        this.dealType = dealType;
        this.price = price;
        this.currency = currency;
        this.totalDuration = totalDuration;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.refundable = refundable;
        this.isRefundable = isRefundable;
        this.airlineName = airlineName;
        this.imageUrl = imageUrl;
        this.segments = segments;
        this.dbId = dbId;
    }


    public static Flight fromHashMap(HashMap<String, Object> map) {
        Flight flight = new Flight();
        flight.dealType = (String) map.get("dealType");
        flight.price = ((Number) map.get("price")).intValue();
        flight.currency = (String) map.get("currency");
        flight.totalDuration = (String) map.get("totalDuration");
        flight.departureDate = (String) map.get("departureDate");
        flight.departureTime = (String) map.get("departureTime");
        flight.refundable = (boolean) map.get("refundable");
        flight.isRefundable = (String) map.get("isRefundable");
        flight.airlineName = (String) map.get("airlineName");
        flight.imageUrl = (String) map.get("imageUrl");

        ArrayList<Segment> segments = new ArrayList<>();
        ArrayList<HashMap<String, Object>> segmentMaps = (ArrayList<HashMap<String, Object>>) map.get("segments");
        if (segmentMaps != null) {
            for (HashMap<String, Object> segmentMap : segmentMaps) {
                segments.add(Segment.fromHashMap(segmentMap));
            }
        }
        flight.segments = segments;

        return flight;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Flight{")
                .append("dealType='").append(dealType).append('\'')
                .append(", price=").append(price)
                .append(", currency='").append(currency).append('\'')
                .append(", totalDuration='").append(totalDuration).append('\'')
                .append(", departureDate='").append(departureDate).append('\'')
                .append(", departureTime='").append(departureTime).append('\'')
                .append(", refundable=").append(refundable)
                .append(", isRefundable='").append(isRefundable).append('\'')
                .append(", airlineName='").append(airlineName).append('\'')
                .append(", imageUrl='").append(imageUrl).append('\'')
                .append(", segments=[");

        if (segments != null) {
            for (Segment segment : segments) {
                sb.append("\n  ").append(segment.toString());
            }
        }
        sb.append("\n]}");
        return sb.toString();
    }

    public static HashMap<String, Object> toHashMap(Flight flight) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("dealType", flight.dealType);
        map.put("price", flight.price);
        map.put("currency", flight.currency);
        map.put("totalDuration", flight.totalDuration);
        map.put("departureDate", flight.departureDate);
        map.put("departureTime", flight.departureTime);
        map.put("refundable", flight.refundable);
        map.put("isRefundable", flight.isRefundable);
        map.put("airlineName", flight.airlineName);
        map.put("imageUrl", flight.imageUrl);
        ArrayList<HashMap<String, Object>> segmentMaps = new ArrayList<>();
        for (Segment segment : flight.segments) {
            segmentMaps.add(segment.toHashMap());
        }
        map.put("segments", segmentMaps);
        return map;
    }

    public static void deleteFlight(String tripId, String flightId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("trips").document(tripId).collection("flights").document(flightId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Flight", "Flight successfully deleted!");
                })
                .addOnFailureListener(e -> {
                    Log.w("Flight", "Error deleting flight", e);
                });
    }
}

