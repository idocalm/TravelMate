package com.idocalm.travelmate;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

public class HotelsSearchFragment extends Fragment {

    Button peopleAmount;
    int amount = 1;


    public HotelsSearchFragment() {
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
        View view = inflater.inflate(R.layout.fragment_hotels_search, container, false);

        AutoCompleteTextView location = view.findViewById(R.id.countries);
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.countries));
        location.setAdapter(locationAdapter);


        peopleAmount = view.findViewById(R.id.amount);
        peopleAmount.setText("1 Person");

        peopleAmount.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Select amount of people");
            builder.setMessage("How many people are you booking for?");

            /* add a + and - button to increase and decrease the amount of people */
            builder.setPositiveButton("+", (dialog, which) -> {
                amount++;
                peopleAmount.setText(amount + " People");
            });

            builder.setNegativeButton("-", (dialog, which) -> {
                if (amount > 1) {
                    amount--;
                    if (amount == 1) {
                        peopleAmount.setText(amount + " Person");
                    } else {
                        peopleAmount.setText(amount + " People");
                    }
                }
            });

            builder.show();
        });


        return view;

    }
}