package com.idocalm.travelmate.models;

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
    }


    public Flight() {
    }

    public Flight(String dealType, int price, String currency, String totalDuration,
                 String departureDate, String departureTime, boolean refundable,
                 String isRefundable, String airlineName, String imageUrl,
                 ArrayList<Segment> segments) {
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
}

