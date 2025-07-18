package com.idocalm.travelmate.cards;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.idocalm.travelmate.ManageTripActivity;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.models.Trip;

import java.text.SimpleDateFormat;

public class TripCard extends Fragment {

    private Trip trip;
    public TripCard(Trip trip) {
        this.trip = trip;
    }

    public TripCard() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_card, container, false);

        TextView name = view.findViewById(R.id.trip_name);
        TextView date = view.findViewById(R.id.trip_date);
        TextView activities = view.findViewById(R.id.trip_activities_amount);
        ImageView memberIndicator = view.findViewById(R.id.member_indicator);

        name.setText(trip.getName());
        activities.setText(trip.getActivities().size() + (trip.getActivities().size() == 1 ? " Activity" : " Activities"));
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
        String tripDate = formatter.format(trip.getStartDate().toDate()) + " - " + formatter.format(trip.getEndDate().toDate());
        date.setText(tripDate);

        Glide.with(getContext())
                .load(trip.getImage())
                .placeholder(R.drawable.trip_placeholder)
                .into((ImageView) view.findViewById(R.id.trip_image));

        // Show member indicator if user is a member but not the owner
        if (!trip.getOwner().equals(Auth.getUser().getId()) && trip.getMembers().contains(Auth.getUser().getId())) {
            memberIndicator.setVisibility(View.VISIBLE);
        }

        Log.d("TripCard", "Trip ID: " + trip.getId());
        Log.d("TripCard", "Trip Owner: " + trip.getOwner());
        Log.d("TripCard", "Current User ID: " + Auth.getUser().getId());
        // Handle click based on user's role
        if (trip.getOwner().equals(Auth.getUser().getId())) {
            // Owner can manage the trip
            view.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ManageTripActivity.class);
                intent.putExtra("trip_id", trip.getId());
                startActivity(intent);
            });
        } else if (trip.getMembers().contains(Auth.getUser().getId())) {
            // Member can view the trip
            view.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ManageTripActivity.class);
                intent.putExtra("trip_id", trip.getId());
                intent.putExtra("is_member", true);
                startActivity(intent);
            });
        }

        Log.d("TripCard", "Owner: " + trip.getOwner());
        Log.d("TripCard", "Auth: " + Auth.getUser().getId());

        return view;
    }
}