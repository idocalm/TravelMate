package com.idocalm.travelmate.components.home;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.cards.TripCard;
import com.idocalm.travelmate.enums.TripVisibility;
import com.idocalm.travelmate.models.Trip;

import java.util.ArrayList;

public class RecentlyViewed extends Fragment {

    private Trip[] recentlyViewedTrips;
    public RecentlyViewed() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d("RecentlyViewed", "User: " + Auth.getUser().getName());

    }

    public void setRecentlyViewedTrips(Trip[] trips) {
        // get the recently viewed trips from the database
        recentlyViewedTrips = trips;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.d("RecentlyViewed", "User: " + Auth.getUser().getName());
        View view = inflater.inflate(R.layout.fragment_recently_viewed, container, false);



        ArrayList<String> arr = Auth.getUser().getTripIds();

        LinearLayout tripsContainer = view.findViewById(R.id.recently_viewed_trips);
        LinearLayout parent = view.findViewById(R.id.recently_viewed_main);
        if (arr.isEmpty()) {

            parent.setVisibility(View.GONE);
        } else {
            parent.setVisibility(View.VISIBLE);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (String tripId : arr) {
            Log.d("RecentlyViewed", "Trip ID: " + tripId);
            // get the trip from the database
            db.collection("trips").document(tripId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    int visibility = Integer.parseInt(task.getResult().get("visibility").toString());
                    Trip trip = new Trip(
                            task.getResult().getString("name"),
                            task.getResult().getString("destination"),
                            task.getResult().getString("owner"),
                            task.getResult().getString("description"),
                            task.getResult().getString("image"),
                            visibility,
                            visibility == TripVisibility.PUBLIC.ordinal() ? new ArrayList<>() : (ArrayList<String>) task.getResult().get("members"),
                            task.getResult().getTimestamp("start_date"),
                            task.getResult().getTimestamp("end_date"),
                            task.getResult().getTimestamp("created_at"),
                            task.getResult().getTimestamp("last_edited"),
                            task.getResult().getTimestamp("last_opened"));
                    Log.d("RecentlyViewed", "Trip: " + trip.getName());
                    // create a new trip card
                    TripCard tripCard = new TripCard(trip);

                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.add(tripsContainer.getId(), tripCard, "card_id" + tripId);

                    transaction.commit();
                    // add the trip card to the container

                }
            });
        }

        return view;
    }
}