package com.idocalm.travelmate.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

/**
 * The type Itinerary activity.
 */
public class ItineraryActivity {

    private String name;
    private String location;
    private Timestamp start_date;
    private Long duration;

    private String note;
    private String cost;

    /**
     * Instantiates a new Itinerary activity.
     *
     * @param name       the name
     * @param location   the location
     * @param start_date the start date
     * @param duration   the duration
     * @param note       the note
     * @param cost       the cost
     */
    public ItineraryActivity(String name, String location, Timestamp start_date, Long duration, String note, String cost) {
        this.name = name;
        this.location = location;
        this.start_date = start_date;
        this.duration = duration;
        this.note = note;
        this.cost = cost;
    }

    /**
     * From db itinerary activity.
     *
     * @param snapshot the snapshot
     * @return the itinerary activity
     */
    public static ItineraryActivity fromDB(DocumentSnapshot snapshot) {
        return new ItineraryActivity(
                snapshot.getString("name"),
                snapshot.getString("location"),
                snapshot.getTimestamp("start_date"),
                snapshot.getLong("duration"),
                snapshot.getString("note"),
                snapshot.getString("cost")
        );
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
                "start_date", start_date,
                "duration", duration,
                "note", note == null ? "" : note,
                "cost", cost == null ? "" : cost
        );
    }

    public static ItineraryActivity fromMap(Map<String, Object> map) {
        return new ItineraryActivity(
                (String) map.get("name"),
                (String) map.get("location"),
                (Timestamp) map.get("start_date"),
                (Long) map.get("duration"),
                (String) map.get("note"),
                (String) map.get("cost")
        );
    }


    public Timestamp getDate() {
        return start_date;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public Duration getDuration() {
        return Duration.ofMillis(duration);
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
}
