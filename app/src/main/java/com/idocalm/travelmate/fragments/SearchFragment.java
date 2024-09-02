package com.idocalm.travelmate.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.idocalm.travelmate.R;
import com.idocalm.travelmate.enums.SearchType;

public class SearchFragment extends Fragment {


    SearchType searchType;
    ImageButton publicButton, privateButton;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        publicButton = view.findViewById(R.id.global_search);
        privateButton = view.findViewById(R.id.private_search);

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