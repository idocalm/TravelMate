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

import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.cards.TripCard;
import com.idocalm.travelmate.enums.SearchType;
import com.idocalm.travelmate.models.Trip;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {


    SearchType searchType;
    ImageButton publicButton, privateButton;
    EditText searchInput;
    List<Trip> searchResult = new ArrayList<>();
    LinearLayout searchResults;


    private void setSearchType(SearchType searchType) {
        this.searchType = searchType;
        if (searchType == SearchType.PUBLIC) {
            publicButton.setBackground(getResources().getDrawable(R.drawable.radius_transparent_selected));
            privateButton.setBackground(getResources().getDrawable(R.drawable.radius_transparent));
        } else {
            publicButton.setBackground(getResources().getDrawable(R.drawable.radius_transparent));
            privateButton.setBackground(getResources().getDrawable(R.drawable.radius_transparent_selected));
        }
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void updateSearchResults() {
        searchResults.removeAllViews();
        searchResults.invalidate();

        for (Trip trip : searchResult) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.add(R.id.search_results, new TripCard(trip));
            ft.commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        publicButton = view.findViewById(R.id.global_search);
        privateButton = view.findViewById(R.id.private_search);
        searchInput = view.findViewById(R.id.search_input);
        searchResults = view.findViewById(R.id.search_results);

        TextView searchTitle = view.findViewById(R.id.search_results_title);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                String query = searchInput.getText().toString();
                // search the trips based on the query and the search type
                db.collection("trips").whereEqualTo("name",  query)
                        .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        searchResult.clear();

                        if (task.getResult().size() == 0) {
                            searchTitle.setText("No results found");
                        } else {
                            searchTitle.setText("Found " + task.getResult().size() + " results");
                        }

                        Toast.makeText(getContext(), "found " + task.getResult().size() + " results", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < task.getResult().size(); i++) {
                            Log.d("SearchFragment", "Trip " + i + ": " + task.getResult().getDocuments().get(i).getString("name"));
                            searchResult.add(Trip.fromDB(task.getResult().getDocuments().get(i)));
                        }
                        updateSearchResults();

                    } else {
                        Toast.makeText(getContext(), "Search failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return false;
        });

        setSearchType(SearchType.PUBLIC);

        publicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchType(SearchType.PUBLIC);
            }
        });

        privateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchType(SearchType.PRIVATE);
            }
        });

        return view;




    }
}