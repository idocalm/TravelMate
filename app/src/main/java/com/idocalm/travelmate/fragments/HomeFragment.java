package com.idocalm.travelmate.fragments;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.components.home.RecentlyViewed;
import com.idocalm.travelmate.components.home.TotalBalanceFragment;
import com.idocalm.travelmate.models.Trip;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HomeFragment extends Fragment {


    public HomeFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static String getTimeRemainingString(Timestamp now, Timestamp tripTime) {
        long diffMillis = (tripTime.getSeconds() - now.getSeconds()) * 1000;

        if (diffMillis <= 0) {
            return "Your trip has already started!";
        }

        long diffDays = diffMillis / (1000 * 60 * 60 * 24);
        if (diffDays >= 1) {
            return "Only " + diffDays + (diffDays == 1 ? " day" : " days") + " left to your next trip";
        }

        long diffHours = diffMillis / (1000 * 60 * 60);
        if (diffHours >= 1) {
            return "Only " + diffHours + (diffHours == 1 ? " hour" : " hours") + " left to your next trip";
        }

        return "Less than 1 hour left to your next trip";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        getChildFragmentManager().beginTransaction().replace(R.id.recently_viewed_container, new RecentlyViewed()).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.total_balance_container, new TotalBalanceFragment()).commit();

        String name = Auth.getUser().getName();

        /* check if the current time is between 7:00 and 12:00 */
        String greeting = "";
        int hour = LocalTime.now().getHour();
        if (hour >= 7 && hour < 12) {
            greeting = "Good morning";
        } else if (hour >= 12 && hour < 18) {
            greeting = "Good afternoon";
        } else {
            greeting = "Good evening";
        }

        TextView greetingTextView = view.findViewById(R.id.greeting);
        greetingTextView.setText(greeting);

        TextView nameTextView = view.findViewById(R.id.welcome_name);
        nameTextView.setText("Hello, " + name);

        TextView leftToTrip = view.findViewById(R.id.left_to_trip);

        if (Auth.getUser().getTripIds() == null || Auth.getUser().getTripIds().isEmpty()) {
            leftToTrip.setVisibility(View.GONE);
            return view;
        }

        ArrayList<String> tripIds = Auth.getUser().getTripIds();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<Trip> loadedTrips = new ArrayList<>();

        for (String tripId : tripIds) {
            Auth.getUser().getTrip(tripId, new Trip.TripCallback() {
                @Override
                public void onTripLoaded(Trip trip) {
                    loadedTrips.add(trip);

                    // After all trips are loaded, find the closest one
                    if (loadedTrips.size() == tripIds.size()) {
                        Trip closestTrip = null;
                        for (Trip t : loadedTrips) {
                            if (trip == null)
                                Log.d("HomeFragment", "Trip is null for ID: " + tripId);
                            if ((closestTrip == null || t.getStartDate().compareTo(closestTrip.getStartDate()) < 0) && t.getStartDate().compareTo(Timestamp.now()) > 0) {
                                closestTrip = t;
                            }
                        }

                        if (closestTrip != null) {

                            String time = getTimeRemainingString(Timestamp.now(), closestTrip.getStartDate());
                            SpannableString spannable = new SpannableString(time);

                            Pattern pattern = Pattern.compile("\\d+\\s+(days|day|hours|hour)");
                            Matcher matcher = pattern.matcher(time);

                            /*


                            if (matcher.find()) {
                                int start = matcher.start();
                                int end = matcher.end();
                                spannable.setSpan(
                                        new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.green)),
                                        start,
                                        end,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                );
                            }
                             */

                            leftToTrip.setText(spannable);
                            leftToTrip.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onError(Exception e) {}
            });
        }

        return view;

    }
}