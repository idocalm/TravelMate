package com.idocalm.travelmate.models;

import android.util.Log;

import com.google.firebase.Timestamp;

import java.time.Duration;
import java.util.Map;

/**
 * The type Itinerary activity.
 */
public class ItineraryActivity {

    private String name;
    private String location;
    private Timestamp date;

    private String note;
    private String cost;

    /**
     * Instantiates a new Itinerary activity.
     *
     * @param name       the name
     * @param location   the location
     * @param date       the date
     * @param note       the note
     * @param cost       the cost
     */
    public ItineraryActivity(String name, String location, Timestamp date, String note, String cost) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.note = note;
        this.cost = cost;
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
                "cost", cost == null ? "" : cost
        );
    }

    public static ItineraryActivity fromMap(Map<String, Object> map) {
        return new ItineraryActivity(
                (String) map.get("name"),
                (String) map.get("location"),
                (Timestamp) map.get("date"),
                (String) map.get("note"),
                (String) map.get("cost")
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

    public String getCost() {
        return cost;
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
        return cost.equals(that.cost);
    }
}
