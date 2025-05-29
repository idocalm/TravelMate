package com.idocalm.travelmate.cards;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.ManageTripActivity;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.dialogs.TripSearchDialog;
import com.idocalm.travelmate.models.Trip;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class SearchTripCard extends Fragment {

    private Trip trip;
    public SearchTripCard(Trip trip) {
        this.trip = trip;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_search_card, container, false);

        TextView name = view.findViewById(R.id.trip_name);
        TextView date = view.findViewById(R.id.trip_date);
        TextView locations = view.findViewById(R.id.trip_locations_amount);

        name.setText(trip.getName());
        if (trip.getName().length() > 20) {
            name.setText(trip.getName().substring(0, 20) + "...");
        }
        locations.setText(trip.getActivities().size() + (trip.getActivities().size() == 1 ? " Location" : " Locations"));
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
        String tripDate = formatter.format(trip.getStartDate().toDate()) + " - " + formatter.format(trip.getEndDate().toDate());
        date.setText(tripDate);

        Glide.with(getContext())
                .load(trip.getImage())
                .placeholder(R.drawable.trip_placeholder)
                .error(R.drawable.trip_placeholder)
                .into((ImageView) view.findViewById(R.id.trip_image));


        if (trip.getOwner().equals(Auth.getUser().getId())) {
            view.findViewById(R.id.trip_result_peek).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.trip_result_duplicate).setVisibility(View.INVISIBLE);


            view.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ManageTripActivity.class);
                intent.putExtra("trip_id", trip.getId());
                startActivity(intent);
            });
            return view;
        }

        view.findViewById(R.id.trip_result_peek).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Trip Details");
            builder.setMessage("Trip Name: " + trip.getName() + "\n" +
                    "Trip Owner: " + trip.getOwner() + "\n" +
                    "Trip Start Date: " + trip.getStartDate().toDate() + "\n" +
                    "Trip End Date: " + trip.getEndDate().toDate() + "\n" +
                    "Trip Locations: " + trip.getActivities().size());
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        view.findViewById(R.id.trip_result_duplicate).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Duplicate Trip");
            builder.setMessage("Are you sure you want to duplicate this trip?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                // Duplicate the trip
                Trip newTrip = new Trip(trip);
                newTrip.setOwner(Auth.getUser().getId());
                newTrip.setId(null);

                HashMap<String, Object> tripMap = Trip.toHashMap(newTrip);

                // Save the new trip to the database
                FirebaseFirestore.getInstance().collection("trips")
                        .add(tripMap).addOnSuccessListener(documentReference -> {
                            Log.d("Trip", "Trip duplicated with ID: " + documentReference.getId());
                            newTrip.setId(documentReference.getId());
                            documentReference.update("tripId", documentReference.getId());
                            Auth.getUser().addTripId(documentReference.getId());
                            Toast.makeText(getContext(), "Trip duplicated successfully", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Log.w("Trip", "Error duplicating trip", e);
                            Toast.makeText(getContext(), "Error duplicating trip", Toast.LENGTH_SHORT).show();

                        });

            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        return view;
    }
}