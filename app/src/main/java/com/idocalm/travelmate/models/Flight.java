package com.idocalm.travelmate.models;

import java.util.ArrayList;

/**
 * The type Flight.
 */
public class Flight {
    /**
     * The Deal type.
     */
    public String dealType;
    /**
     * The Price.
     */
    public int price;
    /**
     * The Currency.
     */
    public String currency;
    /**
     * The Total duration.
     */
    public String totalDuration;
    /**
     * The Departure date.
     */
    public String departureDate;
    /**
     * The Departure time.
     */
    public String departureTime;
    /**
     * The Refundable.
     */
    public boolean refundable;
    /**
     * The Is refundable.
     */
    public String isRefundable;
    /**
     * The Airline name.
     */
    public String airlineName;
    /**
     * The Image url.
     */
    public String imageUrl;
    /**
     * The Segments.
     */
    public ArrayList<Segment> segments;

    /**
     * The type Segment.
     */
    public static class Segment {
        /**
         * The Airline.
         */
        public String airline;
        /**
         * The Flight number.
         */
        public String flightNumber;
        /**
         * The Origin.
         */
        public String origin;
        /**
         * The Destination.
         */
        public String destination;
        /**
         * The Departure date.
         */
        public String departureDate;
        /**
         * The Departure time.
         */
        public String departureTime;
        /**
         * The Arrival date.
         */
        public String arrivalDate;
        /**
         * The Arrival time.
         */
        public String arrivalTime;
        /**
         * The Duration.
         */
        public String duration;
        /**
         * The Cabin.
         */
        public String cabin;
        /**
         * The Aircraft.
         */
        public String aircraft;
        /**
         * The Terminal from.
         */
        public String terminalFrom;
        /**
         * The Terminal to.
         */
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


    public Flight() {
        // Default constructor
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
}

