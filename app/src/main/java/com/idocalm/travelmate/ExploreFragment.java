package com.idocalm.travelmate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ExploreFragment extends Fragment {


    public ExploreFragment() {
        // Required empty public constructor
    }

    Button hotels, flights, attractions, restaurants, rentals;
    Button selectedButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void setButton(Button button) {
        if (selectedButton != null) {
            selectedButton.setBackground(getResources().getDrawable(R.drawable.radius_xl));
        }
        selectedButton = button;
        selectedButton.setBackground(getResources().getDrawable(R.drawable.radius_xl_selected));

        if (selectedButton == hotels) {
            getChildFragmentManager().beginTransaction().replace(R.id.explore_fragment_container, new HotelsSearchFragment()).commit();

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
        attractions = view.findViewById(R.id.attractions);
        restaurants = view.findViewById(R.id.restaurants);
        rentals = view.findViewById(R.id.rentals);

        hotels.setOnClickListener(buttonListener);
        flights.setOnClickListener(buttonListener);
        attractions.setOnClickListener(buttonListener);
        restaurants.setOnClickListener(buttonListener);
        rentals.setOnClickListener(buttonListener);


        return view;


    }
}