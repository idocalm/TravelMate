package com.idocalm.travelmate.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idocalm.travelmate.R;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.models.User;


public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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
        nameTextView.setText(name);




        return view;
    }
}