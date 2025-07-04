package com.idocalm.travelmate.cards;

import android.app.AlertDialog;
import android.content.Intent;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.ManageTripActivity;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.models.Trip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        TextView activities = view.findViewById(R.id.trip_activities_amount);

        name.setText(trip.getName());
        if (trip.getName().length() > 20) {
            name.setText(trip.getName().substring(0, 20) + "...");
        }

        Log.d("TripCard", "Trip activities size: " + trip.getActivities().size());
        activities.setText(trip.getActivities().size() + (trip.getActivities().size() == 1 ? " Activity" : " Activities"));
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
            Intent intent = new Intent(getActivity(), ManageTripActivity.class);
            intent.putExtra("trip_id", trip.getId());
            intent.putExtra("is_peak", true);

            startActivity(intent);
        });

        view.findViewById(R.id.trip_result_duplicate).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Duplicate Trip");
            builder.setMessage("Are you sure you want to duplicate this trip?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                // Duplicate the trip
                Trip newTrip = new Trip(trip);
                newTrip.setOwner(Auth.getUser().getId());
                ArrayList<String> members = new ArrayList<>();
                members.add(Auth.getUser().getId());
                newTrip.setMembers(members);
                newTrip.setId(null);

                HashMap<String, Object> tripMap = Trip.toHashMap(newTrip);

                // Save the new trip to the database
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("trips")
                        .add(tripMap).addOnSuccessListener(documentReference -> {
                            Log.d("Trip", "Trip duplicated with ID: " + documentReference.getId());
                            newTrip.setId(documentReference.getId());
                            documentReference.update("id", documentReference.getId());

                            db.collection("trips").document(trip.getId()).collection("hotels").get().addOnSuccessListener(querySnapshot -> {
                                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                    db.collection("trips").document(documentReference.getId())
                                            .collection("hotels").document(doc.getId())
                                            .set(doc.getData());
                                }
                            });

                            db.collection("trips").document(trip.getId()).collection("flights").get().addOnSuccessListener(querySnapshot -> {
                                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                    db.collection("trips").document(documentReference.getId())
                                            .collection("flights").document(doc.getId())
                                            .set(doc.getData());
                                }
                            });


                            Auth.getUser().addTripId(documentReference.getId());

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