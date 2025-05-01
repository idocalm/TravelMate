package com.idocalm.travelmate.components.explore;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.models.Flight;

import java.util.ArrayList;

public class FlightsListAdapter extends ArrayAdapter<Flight> {

    public FlightsListAdapter(Context context, ArrayList<Flight> flights) {
        super(context, 0, flights);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Flight flight = getItem(position);
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_flight_card, parent, false);
        }

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

        return view;
    }
}
