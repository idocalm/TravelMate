package com.idocalm.travelmate.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.idocalm.travelmate.components.explore.FlightsSearchFragment;
import com.idocalm.travelmate.components.explore.HotelsSearchFragment;
import com.idocalm.travelmate.R;

public class ExploreFragment extends Fragment {
    public ExploreFragment() {}

    Button hotels, flights;
    Button selectedButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void setButton(Button button) {
        if (selectedButton != null) {
            selectedButton.setBackground(getResources().getDrawable(R.drawable.radius));
        }
        selectedButton = button;
        selectedButton.setBackground(getResources().getDrawable(R.drawable.radius_selected));

        if (selectedButton == hotels) {
            getChildFragmentManager().beginTransaction().replace(R.id.explore_fragment_container, new HotelsSearchFragment()).commit();
        } else if (selectedButton == flights) {
            getChildFragmentManager().beginTransaction().replace(R.id.explore_fragment_container, new FlightsSearchFragment()).commit();
        }
    }

    View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setButton((Button) v);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        hotels = view.findViewById(R.id.hotels);
        flights = view.findViewById(R.id.flights);

        hotels.setOnClickListener(buttonListener);
        flights.setOnClickListener(buttonListener);

        return view;

    }
}