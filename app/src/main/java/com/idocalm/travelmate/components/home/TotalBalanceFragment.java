package com.idocalm.travelmate.components.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.models.User;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TotalBalanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TotalBalanceFragment extends Fragment {


    public TotalBalanceFragment() {
        // Required empty public constructor
    }

    public static TotalBalanceFragment newInstance(String param1, String param2) {
        TotalBalanceFragment fragment = new TotalBalanceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_total_balance, container, false);

        User user = Auth.getUser();
        ArrayList<String> ids = user.getTripIds();


        // Get the total balance of all trips
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (ids.size() > 0) {
            // Get the trip balance
            db.collection("trips").document(ids.get(0)).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    double flights = task.getResult().getDouble("total_flights");
                    double hotels = task.getResult().getDouble("total_hotels");
                    double other = task.getResult().getDouble("total_other");

                    Log.d("TotalBalanceFragment", "Flights: " + flights + " Hotels: " + hotels + " Other: " + other);

                    double balance = flights + hotels + other;

                    TextView totalBalanceText = view.findViewById(R.id.total_balance);
                    totalBalanceText.setText("₪ " + balance);
                    TextView otherText = view.findViewById(R.id.balance_other);
                    otherText.setText("₪ " + other);
                }
            });
        }





        return view;

    }
}