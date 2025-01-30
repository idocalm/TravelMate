package com.idocalm.travelmate.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

public class ItineraryActivity {

    private String name;
    private String location;
    private Timestamp start_date;
    private Long duration;

    private String note;
    private String cost;

    public ItineraryActivity(String name, String location, Timestamp start_date, Long duration, String note, String cost) {
        this.name = name;
        this.location = location;
        this.start_date = start_date;
        this.duration = duration;
        this.note = note;
        this.cost = cost;
    }

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


}
