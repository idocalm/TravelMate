package com.idocalm.travelmate.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.models.Flight;
import com.idocalm.travelmate.models.Hotel;

import java.util.ArrayList;
import java.util.HashMap;

public class FlightsListAdapter extends ArrayAdapter<Flight> {

    private boolean isTripView;
    private String tripId;
    private boolean disableEdits;

    public FlightsListAdapter(Context context, ArrayList<Flight> flights, boolean isTripView, String tripId, boolean disableEdits) {
        super(context, 0, flights);
        this.isTripView = isTripView;
        this.tripId = tripId;
        this.disableEdits = disableEdits;

        Log.d("FlightsListAdapter", "Initialized with isTripView: " + isTripView + ", tripId: " + tripId);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Flight flight = getItem(position);

        if (this.isTripView) {
            Log.d("FlightsListAdapter", "Setting up trip view for flight: " + flight.airlineName);
            return setupTripView(flight, parent);
        }

        Log.d("FlightsListAdapter", "Setting up normal view for flight: " + flight.airlineName);

        return setupNormalView(flight, parent);


    }

    public View setupTripView(Flight flight, ViewGroup parent) {

        Log.d("FlightsListAdapter", "Flight details: " + flight.toString());

        Log.d("FlightsListAdapter", "Setting up trip view for flight: " + flight.airlineName);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_trip_flight_card, parent, false);

        TextView airline = view.findViewById(R.id.airline);
        airline.setText(flight.airlineName);

        TextView flightDates = view.findViewById(R.id.flight_dates);
        flightDates.setText(flight.departureDate);

        TextView flightDuration = view.findViewById(R.id.flight_duration);
        flightDuration.setText(flight.totalDuration + " • " + (flight.segments.isEmpty() ? "" : flight.segments.get(0).cabin));

        TextView priceText = view.findViewById(R.id.flight_price);
        priceText.setText(flight.currency + " " + flight.price);

        ImageView logo = view.findViewById(R.id.airline_logo);
        Glide.with(getContext())
                .load(flight.imageUrl)
                .placeholder(R.drawable.profile_icon) // Placeholder image
                .into(logo);

        if (!disableEdits) {
            view.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Long press to remove", Toast.LENGTH_SHORT).show();
            });
        }

        if (!disableEdits) {
            view.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Delete Flight");
                builder.setMessage("Are you sure you want to delete this flight?");
                builder.setPositiveButton("Delete", (dialog, which) -> {
                    dialog.dismiss();

                    Flight.deleteFlight(tripId, flight.dbId);
                    Toast.makeText(getContext(), "Flight deleted", Toast.LENGTH_SHORT).show();
                    ((Activity) getContext()).recreate();
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            });
        }





        return view;
    }

    public View setupNormalView(Flight flight, ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_flight_card, parent, false);
        // Bind views
        ImageView logo = view.findViewById(R.id.airline_logo);
        TextView airlineName = view.findViewById(R.id.airline_name);
        TextView flightDuration = view.findViewById(R.id.flight_duration);
        TextView priceText = view.findViewById(R.id.price);
        LinearLayout segmentLayout = view.findViewById(R.id.segment_timeline);

        // Set content
        Glide.with(getContext())
                .load(flight.imageUrl)
                .placeholder(R.drawable.profile_icon) // Placeholder image
                .into(logo);

        airlineName.setText(flight.airlineName);
        flightDuration.setText(flight.totalDuration + " • " + (flight.segments.isEmpty() ? "" : flight.segments.get(0).cabin));
        priceText.setText(flight.currency + " " + flight.price);

        // Clear and repopulate timeline
        segmentLayout.removeAllViews();
        for (int i = 0; i < flight.segments.size(); i++) {
            Flight.Segment segment = flight.segments.get(i);
            TextView segmentView = new TextView(getContext());
            segmentView.setText(segment.origin + " → " + segment.destination);
            segmentView.setPadding(30, 8, 30, 8);
            segmentView.setTextColor(Color.WHITE);
            segmentView.setBackgroundResource(R.drawable.timeline_bubble); // create this drawable
            segmentLayout.addView(segmentView);
        }

        TextView dealLabel = view.findViewById(R.id.deal_type_label);
        String dealType = flight.dealType != null ? flight.dealType.trim() : "";

        switch (dealType) {
            case "Best Deal":
                dealLabel.setText("BEST DEAL");
                dealLabel.setBackgroundColor(Color.parseColor("#2E7D32")); // Green
                dealLabel.setVisibility(View.VISIBLE);
                break;
            case "Preferred":
                dealLabel.setText("PREFERRED");
                dealLabel.setBackgroundColor(Color.parseColor("#0277BD")); // Blue
                dealLabel.setVisibility(View.VISIBLE);
                break;
            default:
                dealLabel.setVisibility(View.GONE); // Hide for "None" or null
                break;
        }

        view.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Long press to bind with flight", Toast.LENGTH_SHORT).show();
        });

        view.setOnLongClickListener(v -> {
            ArrayList<String> tripIds = Auth.getUser().getTripIds();
            if (tripIds.isEmpty()) {
                Toast.makeText(getContext(), "You need to create a trip first", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                HashMap<String, String> trips = new HashMap<>();
                for (String tripId : tripIds) {
                    db.collection("trips").document(tripId).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            trips.put(tripId, task1.getResult().getString("name"));

                            if (trips.size() == tripIds.size()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select a trip");
                                String[] tripNames = new String[trips.size()];

                                int i = 0;
                                for (String id : trips.keySet()) {
                                    tripNames[i] = trips.get(id);
                                    i++;
                                }

                                builder.setItems(tripNames, (dialog, which) -> {
                                    String selectedTripName = tripNames[which];
                                    String selectedTripId = null;

                                    for (String id : trips.keySet()) {
                                        if (trips.get(id).equals(selectedTripName)) {
                                            selectedTripId = id;
                                            break;
                                        }
                                    }

                                    HashMap<String, Object> flightMap = Flight.toHashMap(flight)
                                            ;
                                    db.collection("trips").document(selectedTripId).collection("flights").add(flightMap)
                                            .addOnSuccessListener(documentReference -> {
                                                Toast.makeText(getContext(), "Flight added to trip", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getContext(), "Error adding flight to trip", Toast.LENGTH_SHORT).show();
                                            });
                                });

                                builder.setNegativeButton("Cancel", (dialog, which) -> {
                                    dialog.dismiss();
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                }
            }

            return true;
        });

        return view;
    }
}
