package com.idocalm.travelmate.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.components.home.RecentlyViewed;
import com.idocalm.travelmate.components.home.TotalBalanceFragment;
import com.idocalm.travelmate.models.Trip;
import com.idocalm.travelmate.models.User;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fetchRecentTrips();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        getChildFragmentManager().beginTransaction().replace(R.id.recently_viewed_container, new RecentlyViewed()).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.total_balance_container, new TotalBalanceFragment()).commit();

        String name = Auth.getUser().getName();


        /* check if the current time is between 7:00 and 12:00 */
        String greeting = "";
        int hour = java.time.LocalTime.now().getHour();
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




        return view;
    }
}