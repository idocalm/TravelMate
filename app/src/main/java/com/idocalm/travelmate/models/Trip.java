package com.idocalm.travelmate.models;

import android.util.Log;

import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.cards.TripCard;
import com.idocalm.travelmate.enums.TripVisibility;

import org.w3c.dom.Document;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private ArrayList<Hotel> hotels;
    private ArrayList<Flight> flights;

    public interface TripCallback {
        void onTripLoaded(Trip trip);
        void onError(Exception e);
    }

    public static HashMap<String, Object> toHashMap(Trip t) {
        HashMap<String, Object> trip = new HashMap<>();
        trip.put("name", t.getName());
        trip.put("destination", t.getDestination());
        trip.put("owner", Auth.getUser().getId());
        trip.put("description", t.getDescription());
        trip.put("image", t.getImage());
        trip.put("visibility", TripVisibility.PUBLIC.toString());
        trip.put("members", t.getMembers());
        trip.put("start_date", t.getStartDate());
        trip.put("end_date", t.getEndDate());
        trip.put("created_at", t.getCreatedAt());
        trip.put("last_edited", t.getLastEdited());
        trip.put("last_opened", t.getLastOpened());
        trip.put("itinerary", t.getActivities());
        return trip;
    }



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

    public static void fromDB(DocumentSnapshot snapshot, TripCallback callback) {
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
                new ArrayList<>(), // initially empty
                new ArrayList<>() // initially empty
        );

        getHotelsFromDB(snapshot.getId(), new Hotel.HotelsCallback() {
            @Override
            public void onHotelsLoaded(ArrayList<Hotel> hotels) {
                trip.hotels = hotels;

                getFlightsFromDB(trip.id, new Flight.FlightsCallback() {
                    @Override
                    public void onFlightsLoaded(ArrayList<Flight> flights) {
                        trip.flights = flights;
                        callback.onTripLoaded(trip);
                    }

                    @Override
                    public void onError(Exception e) {
                        callback.onError(e);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });


    }


    public ArrayList<Hotel> getHotels() {
        return hotels;
    }

    public ArrayList<Flight> getFlights() {
        return flights;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public static ArrayList<ItineraryActivity> getActivitiesFromDB(DocumentSnapshot snapshot) {
        ArrayList<ItineraryActivity> activities = new ArrayList<>();

        Object rawActivities = snapshot.get("itinerary");

        if (rawActivities instanceof List<?>) {
            List<?> list = (List<?>) rawActivities;
            for (Object item : list) {
                if (item instanceof Map<?, ?>) {
                    Map<String, Object> activityMap = (Map<String, Object>) item;

                    Object costObj = activityMap.get("cost");
                    Double cost = null;

                    if (costObj instanceof Number) {
                        cost = ((Number) costObj).doubleValue();
                    }

                    activities.add(new ItineraryActivity(
                            (String) activityMap.get("name"),
                            (String) activityMap.get("location"),
                            (Timestamp) activityMap.get("date"),
                            (String) activityMap.get("note"),
                            cost,
                            (String) activityMap.get("currency"),
                            (Timestamp) activityMap.get("createdAt")
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

    public static void getFlightsFromDB(String tripId, Flight.FlightsCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("trips").document(tripId).collection("flights")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Flight> flights = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();

                        if (querySnapshot != null) {
                            for (DocumentSnapshot item : querySnapshot.getDocuments()) {
                                try {
                                    Map<String, Object> flightMap = item.getData();

                                    ArrayList<Flight.Segment> segments = new ArrayList<>();
                                    if (flightMap.get("segments") instanceof List<?>) {
                                        List<?> segmentList = (List<?>) flightMap.get("segments");
                                        for (Object segmentItem : segmentList) {
                                            if (segmentItem instanceof Map<?, ?>) {
                                                Flight.Segment segment = Flight.Segment.fromHashMap((HashMap<String, Object>) segmentItem);
                                                segments.add(segment);
                                            } else {
                                                Log.w("Trip", "Skipping non-map item in segments: " + segmentItem);
                                            }
                                        }
                                    }

                                    Flight flight = Flight.fromHashMap((HashMap<String, Object>) flightMap);
                                    flight.dbId = item.getId();

                                    flights.add(flight);
                                } catch (Exception e) {
                                    Log.w("Trip", "Skipping invalid hotel data: " + item, e);
                                }
                            }
                        }

                        callback.onFlightsLoaded(flights);
                    } else {
                        callback.onError(task.getException());
                    }
                });

    }


    public static void getHotelsFromDB(String tripId, Hotel.HotelsCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("trips").document(tripId).collection("hotels")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Hotel> hotels = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();

                        if (querySnapshot != null) {
                            for (DocumentSnapshot item : querySnapshot.getDocuments()) {
                                try {
                                    Map<String, Object> hotelMap = item.getData();
                                    Double price = Double.parseDouble((String) hotelMap.get("price"));

                                    Hotel hotel = new Hotel(
                                            item.getId(),
                                            (String) hotelMap.get("name"),
                                            (String) hotelMap.get("mainPhoto"),
                                            price,
                                            item.getDate("checkInDate"),
                                            item.getDate("checkOutDate"),
                                            (String) hotelMap.get("currency")
                                    );
                                    hotels.add(hotel);
                                } catch (Exception e) {
                                    Log.w("Trip", "Skipping invalid hotel data: " + item, e);
                                }
                            }
                        }

                        callback.onHotelsLoaded(hotels);
                    } else {
                        callback.onError(task.getException());
                    }
                });
    }



    public ArrayList<Map<String, Object>> getActivitiesHashed() {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        for (ItineraryActivity activity : this.activities) {
            res.add(activity.toMap());
        }

        return res;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public ArrayList<ItineraryActivity> getActivities() {
        return activities;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


    public void addActivity(ItineraryActivity activity) {
        this.activities.add(activity);
        FirebaseFirestore.getInstance().collection("trips").document(this.id).update("itinerary", this.activities);
    }

    public void editActivity(ItineraryActivity old, ItineraryActivity newActivity) {
        Log.d("Trip", "Editing activity: " + old.toMap() + " to " + newActivity.toMap());
        ArrayList<ItineraryActivity> activities = new ArrayList<ItineraryActivity>();
        for (ItineraryActivity act : this.activities) {
            Log.d("Trip", "Activity: " + act.toMap());
            if (act.equals(old)) {
                Log.d("Trip", "REPLACE");
                activities.add(newActivity);
            } else {
                activities.add(act);
            }
        }

        this.activities = activities;

        FirebaseFirestore.getInstance().collection("trips").document(this.id).update("itinerary", this.activities);
    }


    public void removeActivity(ItineraryActivity activity) {
        Log.d("Trip", "Removing activity: " + activity.toMap());
        ArrayList<ItineraryActivity> activities = new ArrayList<ItineraryActivity>();
        for (ItineraryActivity act : this.activities) {
            if (!act.equals(activity)) {
                activities.add(act);
            }
        }

        this.activities = activities;
        Log.d("Trip", "Activities after removal: " + this.activities);

        FirebaseFirestore.getInstance().collection("trips").document(this.id).update("itinerary", this.activities);
    }

    public void save() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> tripData = toHashMap(this);
        db.collection("trips").document(this.id).update(tripData)
            .addOnFailureListener(e -> {
                Log.e("Trip", "Error saving trip: " + e.getMessage());
            });
    }

    public void addMember(String memberId) {
        if (!members.contains(memberId)) {
            members.add(memberId);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("trips").document(this.id).update("members", members)
                .addOnFailureListener(e -> {
                    Log.e("Trip", "Error adding member: " + e.getMessage());
                });
        }
    }

}
