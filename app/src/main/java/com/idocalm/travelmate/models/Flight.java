package com.idocalm.travelmate.models;

import java.util.ArrayList;

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
}

