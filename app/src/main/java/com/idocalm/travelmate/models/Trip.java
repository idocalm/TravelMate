package com.idocalm.travelmate.models;

import android.util.Log;

import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.cards.TripCard;
import com.idocalm.travelmate.enums.TripVisibility;

import org.w3c.dom.Document;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Trip.
 */
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
    private ArrayList<Hotel> hotels;
    private ArrayList<Flight> flights;

    /**
     * To hash map hash map.
     *
     * @param t the t
     * @return the hash map
     */
    public static HashMap<String, Object> toHashMap(Trip t) {
        HashMap<String, Object> trip = new HashMap<>();
        trip.put("name", t.getName());
        trip.put("destination", t.getDestination());
        trip.put("owner", Auth.getUser().getId());
        trip.put("description", t.getDescription());
        trip.put("image", t.getImage());
        trip.put("visibility", TripVisibility.PUBLIC.toString()); // TODO: make this dynamic
        trip.put("members", t.getMembers());
        trip.put("start_date", t.getStartDate());
        trip.put("end_date", t.getEndDate());
        trip.put("created_at", t.getCreatedAt());
        trip.put("last_edited", t.getLastEdited());
        trip.put("last_opened", t.getLastOpened());
        trip.put("itinerary", t.getActivities());
        trip.put("total_flights", 0.0);
        trip.put("total_hotels", 0.0);
        trip.put("total_other", 0.0);

        return trip;
    }


    /**
     * Instantiates a new Trip.
     *
     * @param id          the id
     * @param name        the name
     * @param destination the destination
     * @param owner       the owner
     * @param description the description
     * @param image       the image
     * @param members     the members
     * @param start_date  the start date
     * @param end_date    the end date
     * @param created_at  the created at
     * @param last_edited the last edited
     * @param last_opened the last opened
     * @param activities  the activities
     * @param hotels      the hotels
     * @param flights     the flights
     */
    public Trip(String id, String name, String destination, String owner, String description, String image, ArrayList<String> members, Timestamp start_date, Timestamp end_date, Timestamp created_at, Timestamp last_edited, Timestamp last_opened, ArrayList<ItineraryActivity> activities, ArrayList<Hotel> hotels, ArrayList<Flight> flights) {
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
        this.hotels = hotels;
        this.flights = flights;
    }

    /**
     * Instantiates a new Trip.
     *
     * @param trip the trip
     */
    public Trip(Trip trip) {
        this.id = trip.id;
        this.name = trip.name;
        this.destination = trip.destination;
        this.owner = trip.owner;
        this.description = trip.description;
        this.image = trip.image;
        this.members = trip.members;
        this.start_date = trip.start_date;
        this.end_date = trip.end_date;
        this.created_at = trip.created_at;
        this.last_edited = trip.last_edited;
        this.last_opened = trip.last_opened;
        this.activities = trip.activities;
    }

    /**
     * From db trip.
     *
     * @param snapshot the snapshot
     * @return the trip
     */
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
                getActivitiesFromDB(snapshot),
                getHotelsFromDB(snapshot),
                getFlightsFromDB(snapshot)
        );

        return trip;

    }


    /**
     * Gets members.
     *
     * @return the members
     */
    public ArrayList<String> getMembers() {
        return members;
    }

    /**
     * Gets activities from db.
     *
     * @param snapshot the snapshot
     * @return the activities from db
     */
    public static ArrayList<ItineraryActivity> getActivitiesFromDB(DocumentSnapshot snapshot) {
        ArrayList<ItineraryActivity> activities = new ArrayList<>();

        Object rawActivities = snapshot.get("itinerary");

        if (rawActivities instanceof List<?>) {
            List<?> list = (List<?>) rawActivities;
            for (Object item : list) {
                if (item instanceof Map<?, ?>) {
                    Map<String, Object> activityMap = (Map<String, Object>) item;
                    activities.add(new ItineraryActivity(
                            (String) activityMap.get("name"),
                            (String) activityMap.get("location"),
                            (Timestamp) activityMap.get("start_date"),
                            (Long) activityMap.get("duration"),
                            (String) activityMap.get("note"),
                            (String) activityMap.get("cost")
                    ));
                } else {
                    // (Optional) Log if there's a weird item
                    Log.w("Trip", "Skipping non-map item in itinerary: " + item);
                }
            }
        } else {
            Log.w("Trip", "Itinerary field is missing or not a list");
        }

        return activities;
    }

    /**
     * Gets hotels from db.
     *
     * @param snapshot the snapshot
     * @return the hotels from db
     */
    public static ArrayList<Hotel> getHotelsFromDB(DocumentSnapshot snapshot) {
        ArrayList<Hotel> hotels = new ArrayList<>();

        Object rawHotels = snapshot.get("hotels");

        if (rawHotels instanceof List<?>) {
            List<?> list = (List<?>) rawHotels;
            for (Object item : list) {
                if (item instanceof Map<?, ?>) {
                    Map<String, Object> hotelMap = (Map<String, Object>) item;
                    hotels.add(new Hotel(
                            (Integer) hotelMap.get("id"),
                            (String) hotelMap.get("name"),
                            (String) hotelMap.get("main_photo"),
                            (Long) hotelMap.get("price"),
                            (Date) hotelMap.get("check_in"),
                            (Date) hotelMap.get("check_out")
                    ));
                } else {
                    // (Optional) Log if there's a weird item
                    Log.w("Trip", "Skipping non-map item in hotels: " + item);
                }
            }
        } else {
            Log.w("Trip", "Hotels field is missing or not a list");
        }

        return hotels;
    }

    /**
     * Gets flights from db.
     *
     * @param snapshot the snapshot
     * @return the flights from db
     */
    public static ArrayList<Flight> getFlightsFromDB(DocumentSnapshot snapshot) {
        ArrayList<Flight> flights = new ArrayList<>();

        Object rawFlights = snapshot.get("flights");

        if (rawFlights instanceof List<?>) {
            List<?> list = (List<?>) rawFlights;
            for (Object item : list) {
                if (item instanceof Map<?, ?>) {
                    Map<String, Object> flightMap = (Map<String, Object>) item;
                    flights.add(new Flight(
                            (String) flightMap.get("deal_type"),
                            (Integer) flightMap.get("price"),
                            (String) flightMap.get("currency"),
                            (String) flightMap.get("total_duration"),
                            (String) flightMap.get("departure_date"),
                            (String) flightMap.get("departure_time"),
                            (Boolean) flightMap.get("refundable"),
                            (String) flightMap.get("is_refundable"),
                            (String) flightMap.get("airline_name"),
                            (String) flightMap.get("image_url"),
                            (ArrayList<Flight.Segment>) flightMap.get("segments")
                    ));
                } else {
                    // (Optional) Log if there's a weird item
                    Log.w("Trip", "Skipping non-map item in flights: " + item);
                }
            }
        } else {
            Log.w("Trip", "Flights field is missing or not a list");
        }

        return flights;

    }

    /**
     * Gets activities.
     *
     * @return the activities
     */
    public ArrayList<Map<String, Object>> getActivities() {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        for (ItineraryActivity activity : this.activities) {
            res.add(activity.toMap());
        }

        return res;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets destination.
     *
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Gets owner.
     *
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets image.
     *
     * @return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * Gets start date.
     *
     * @return the start date
     */
    public Timestamp getStartDate() {
        return start_date;
    }

    /**
     * Gets end date.
     *
     * @return the end date
     */
    public Timestamp getEndDate() {
        return end_date;
    }

    /**
     * Gets created at.
     *
     * @return the created at
     */
    public Timestamp getCreatedAt() {
        return created_at;
    }


    /**
     * Gets last edited.
     *
     * @return the last edited
     */
    public Timestamp getLastEdited() {
        return last_edited;
    }

    /**
     * Gets last opened.
     *
     * @return the last opened
     */
    public Timestamp getLastOpened() {
        return last_opened;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets destination.
     *
     * @param dest the dest
     */
    public void setDestination(String dest) {
        this.destination = dest;
    }

    /**
     * Sets owner.
     *
     * @param owner the owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets start date.
     *
     * @param start_date the start date
     */
    public void setStartDate(Timestamp start_date) {
        this.start_date = start_date;
    }

    /**
     * Sets end date.
     *
     * @param end_date the end date
     */
    public void setEndDate(Timestamp end_date) {
        this.end_date = end_date;
    }

    /**
     * Sets created at.
     *
     * @param created_at the created at
     */
    public void setCreatedAt(Timestamp created_at) {
        this.created_at = created_at;
    }

    /**
     * Sets last edited.
     *
     * @param last_edited the last edited
     */
    public void setLastEdited(Timestamp last_edited) {
        this.last_edited = last_edited;
    }

    /**
     * Sets last opened.
     *
     * @param last_opened the last opened
     */
    public void setLastOpened(Timestamp last_opened) {
        this.last_opened = last_opened;
    }

    /**
     * Add activity.
     *
     * @param activity the activity
     */
    public void addActivity(ItineraryActivity activity) {
        this.activities.add(activity);
    }

    /**
     * Add hotel, and save it to the database.
     *
     * @param hotel the hotel
     */
    public void addHotel(Hotel hotel) {
        this.hotels.add(hotel);
        FirebaseFirestore.getInstance().collection("trips").document(this.id).update("hotels", this.hotels);
    }


}
