package com.idocalm.travelmate.models;

import android.util.Log;

import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.cards.TripCard;
import com.idocalm.travelmate.enums.TripVisibility;

import org.w3c.dom.Document;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class Trip {
    private String id;
    private String name;
    private String destination;
    private String owner;
    private String description;
    private Timestamp start_date;
    private Timestamp end_date;
    private Timestamp created_at;
    private Timestamp last_edited;
    private Timestamp last_opened;
    private String image;
    private ArrayList<String> members;
    private ArrayList<ItineraryActivity> activities;



    public Trip(String id, String name, String destination, String owner, String description, String image, ArrayList<String> members, Timestamp start_date, Timestamp end_date, Timestamp created_at, Timestamp last_edited, Timestamp last_opened, ArrayList<ItineraryActivity> activities) {
        this.id = id;
        this.name = name;
        this.destination = destination;
        this.owner = owner;
        this.description = description;
        this.image = image;
        this.members = members;
        this.start_date = start_date;
        this.end_date = end_date;
        this.created_at = created_at;
        this.last_edited = last_edited;
        this.last_opened = last_opened;
        this.activities = activities;
    }

    // write a static function to create a new trip from the db response, without passing each field
    public static Trip fromDB(DocumentSnapshot snapshot) {
        Trip trip = new Trip(
                snapshot.getId(),
                snapshot.getString("name"),
                snapshot.getString("destination"),
                snapshot.getString("owner"),
                snapshot.getString("description"),
                snapshot.getString("image"),
                (ArrayList<String>) snapshot.get("members"),
                snapshot.getTimestamp("start_date"),
                snapshot.getTimestamp("end_date"),
                snapshot.getTimestamp("created_at"),
                snapshot.getTimestamp("last_edited"),
                snapshot.getTimestamp("last_opened"),
                getActivitiesFromDB(snapshot)
        );

        return trip;

    }

    public static ArrayList<ItineraryActivity> getActivitiesFromDB(DocumentSnapshot snapshot) {
        ArrayList<ItineraryActivity> activities = new ArrayList<>();
        // go over "itinerary" array of maps and create an ItineraryActivity object for each map
        ArrayList<Map<String, Object>> activitiesMap = (ArrayList<Map<String, Object>>) snapshot.get("itinerary");
        for (Map<String, Object> activityMap : activitiesMap) {
            activities.add(new ItineraryActivity(
                    (String) activityMap.get("name"),
                    (String) activityMap.get("location"),
                    (Timestamp) activityMap.get("start_date"),
                    (Long) activityMap.get("duration"),
                    (String) activityMap.get("note"),
                    (String) activityMap.get("cost")
            ));
        }
        return activities;
    }

    public ArrayList<Map<String, Object>> getActivities() {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        for (ItineraryActivity activity : this.activities) {
            res.add(activity.toMap());
        }

        return res;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDestination() {
        return destination;
    }

    public String getOwner() {
        return owner;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }
    public Timestamp getStartDate() {
        return start_date;
    }

    public Timestamp getEndDate() {
        return end_date;
    }

    public Timestamp getCreatedAt() {
        return created_at;
    }

    public Timestamp getLastEdited() {
        return last_edited;
    }

    public Timestamp getLastOpened() {
        return last_opened;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDestination(String dest) {
        this.destination = dest;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDate(Timestamp start_date) {
        this.start_date = start_date;
    }

    public void setEndDate(Timestamp end_date) {
        this.end_date = end_date;
    }

    public void setCreatedAt(Timestamp created_at) {
        this.created_at = created_at;
    }

    public void setLastEdited(Timestamp last_edited) {
        this.last_edited = last_edited;
    }

    public void setLastOpened(Timestamp last_opened) {
        this.last_opened = last_opened;
    }

    public void addActivity(ItineraryActivity activity) {
        this.activities.add(activity);
    }

    public void save() {
        // TODO: save the trip to the database
    }




}
