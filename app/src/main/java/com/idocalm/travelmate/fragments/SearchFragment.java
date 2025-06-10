package com.idocalm.travelmate.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.cards.SearchTripCard;
import com.idocalm.travelmate.cards.TripCard;
import com.idocalm.travelmate.models.Trip;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    EditText searchInput;
    List<Trip> searchResult = new ArrayList<>();
    LinearLayout searchResults;

    public SearchFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


private void updateSearchResults() {

        Log.d("SearchFragment", "Updating search results with " + searchResult.size() + " trips");
    searchResults.removeAllViews();
    searchResults.invalidate();

    for (Trip trip : searchResult) {
        Log.d("SearchFragment", "Adding trip: " + trip.getName() + " to search results");
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(R.id.search_results, new SearchTripCard(trip));
        ft.commit();
    }

    searchResults.requestLayout();
    searchResults.setVisibility(View.VISIBLE);
}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_search, container, false);

    searchInput = view.findViewById(R.id.search_input);
    searchResults = view.findViewById(R.id.search_results);

    TextView searchTitle = view.findViewById(R.id.search_results_title);

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    searchInput.setOnEditorActionListener((v, actionId, event) -> {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
            String query = searchInput.getText().toString();
            // Search trips collection to find trips where the destination or name matches the query
            if (query.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a search query", Toast.LENGTH_SHORT).show();
                return false;
            }


            db.collection("trips").get().addOnSuccessListener(querySnapshot -> {
                searchResult.clear();
                List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                if (documents.isEmpty()) {
                    searchTitle.setText("No results found");
                    searchResults.setVisibility(View.GONE);
                    return;
                } else {
                    searchResults.setVisibility(View.VISIBLE);
                }

                ArrayList<Trip> trips = new ArrayList<>();

                for (DocumentSnapshot document : documents) {
                    Trip.fromDB(document, new Trip.TripCallback() {
                        @Override
                        public void onTripLoaded(Trip trip) {
                            if (trip.getName().toLowerCase().contains(query.toLowerCase()) ||
                                    trip.getDestination().toLowerCase().contains(query.toLowerCase())) {
                                searchResult.add(trip);
                            }

                            trips.add(trip);

                            if (trips.size() == documents.size()) {
                                updateSearchResults();
                                searchTitle.setText("Found " + searchResult.size() + " results");
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("SearchFragment", "Error loading trip: " + e.getMessage());
                        }
                    });
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Search failed", Toast.LENGTH_SHORT).show();
            });




        }
        return false;
    });

    return view;

    }
}