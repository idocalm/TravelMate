package com.idocalm.travelmate.cards;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.idocalm.travelmate.ManageTripActivity;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.dialogs.TripSearchDialog;
import com.idocalm.travelmate.models.Trip;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

public class TripCard extends Fragment {

    private Trip trip;
    public TripCard(Trip trip) {
        this.trip = trip;
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setImage(Bitmap image) {
        if (getActivity() == null) return; // avoid crash if activity is not available


         getActivity().runOnUiThread( new Runnable() {
            @Override
            public void run() {
                ImageView imageView = getView().findViewById(R.id.trip_image);
                Log.d("TripCard", "Setting image");
                imageView.setImageBitmap(image);

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trip_card, container, false);

        TextView name = view.findViewById(R.id.trip_name);
        TextView date = view.findViewById(R.id.trip_date);
        TextView locations = view.findViewById(R.id.trip_locations_amount);
        ImageView memberIndicator = view.findViewById(R.id.member_indicator);

        name.setText(trip.getName());
        locations.setText(trip.getActivities().size() + (trip.getActivities().size() == 1 ? " Location" : " Locations"));
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
        } else {
            // Non-member can view trip details
            view.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Long press to view more.", Toast.LENGTH_SHORT).show();
            });

            view.setOnLongClickListener(v -> {
                TripSearchDialog dialog = new TripSearchDialog((Activity) getContext());
                dialog.show();
                return true;
            });
        }

        Log.d("TripCard", "Owner: " + trip.getOwner());
        Log.d("TripCard", "Auth: " + Auth.getUser().getId());

        return view;
    }
}