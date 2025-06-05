package com.idocalm.travelmate.models;

import android.util.Log;

import com.google.firebase.Timestamp;

import java.time.Duration;
import java.util.Map;

public class ItineraryActivity {

    private String name;
    private String location;
    private Timestamp date;

    private String note;
    private Double cost;
    private String currency;
    private Timestamp createdAt;

    public ItineraryActivity(String name, String location, Timestamp date, String note, Double cost, String currency, Timestamp createdAt) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.note = note;
        this.cost = cost;
        this.currency = currency;
        this.createdAt = createdAt;
    }

    /**
     * To map map.
     *
     * @return the map
     */
    public Map<String, Object> toMap() {
        return Map.of(
                "name", name,
                "location", location,
                "date", date,
                "note", note == null ? "" : note,
                "cost", cost == null ? "" : cost,
                "currency", currency == null ? "" : currency
        );
    }

    public static ItineraryActivity fromMap(Map<String, Object> map) {
        return new ItineraryActivity(
                (String) map.get("name"),
                (String) map.get("location"),
                (Timestamp) map.get("date"),
                (String) map.get("note"),
                (Double) map.get("cost"),
                (String) map.get("currency"),
                (Timestamp) map.getOrDefault("createdAt", Timestamp.now())
        );
    }


    public Timestamp getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getNote() {
        return note;
    }

    public double getCost() {
        return cost;
    }

    public String getCurrency() {
        return currency;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ItineraryActivity that = (ItineraryActivity) obj;

        if (!name.equals(that.name)) return false;
        if (!location.equals(that.location)) return false;
        if (!date.equals(that.date)) return false;
        if (!note.equals(that.note)) return false;
        if (!currency.equals(that.currency)) return false;
        return cost.equals(that.cost);
    }

    public Object getCreatedAt() {
        if (createdAt == null) {
            Log.w("ItineraryActivity", "CreatedAt is null, returning current time");
            return Timestamp.now();
        }
        return createdAt;
    }
}
