package com.idocalm.travelmate.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.idocalm.travelmate.MainActivity;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.models.BreadcrumbEvent;

import java.util.ArrayList;



public class BreadcrumbFragment extends Fragment {

    private ArrayList<BreadcrumbEvent> elements = new ArrayList<>();
    private Fragment fragment;
    public Runnable onHomeClick = () -> {};


    public BreadcrumbFragment(ArrayList<BreadcrumbEvent> input, Fragment fragment) {
        this.elements = input;
        this.fragment = fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_breadcrumb, container, false);
        Typeface bold = getResources().getFont(R.font.bold);

        Button logoutButton = view.findViewById(R.id.logout_btn);
        logoutButton.setOnClickListener(v -> {
            Log.d("BreadcrumbFragment", "Logout button clicked");
            Auth.logOut(getActivity());
        });

        LinearLayout contentLayout = view.findViewById(R.id.breadcrumb_content);
        FrameLayout fragmentLayout = view.findViewById(R.id.breadcrumb_fragment);

        if (fragment != null) {
            getChildFragmentManager().beginTransaction().replace(fragmentLayout.getId(), fragment).commit();
        }
        /* insert the layout: first an element, then a separator (an image view with a right_arrow drawable), and keep going until the last element to which you don't add a separator */

        if (elements.isEmpty()) {
            return view;
        }


        ImageView first = new ImageView(getContext());
        first.setImageResource(R.drawable.right_arrow);
        contentLayout.addView(first);

        BreadcrumbEvent firstEvent = elements.get(0);

        TextView textView = new TextView(getContext());
        textView.setText(firstEvent.getTitle());
        textView.setTextSize(15);
        textView.setTypeface(bold);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setOnClickListener(v -> {
            onHomeClick.run();
        });
        contentLayout.addView(textView);

        if (elements.size() == 1) {
            return view;
        }

        first = new ImageView(getContext());
        first.setImageResource(R.drawable.right_arrow);
        contentLayout.addView(first);

        for (int i = 1; i < elements.size() - 1; i++) {
            BreadcrumbEvent event = elements.get(i);

            textView = new TextView(getContext());
            textView.setText(event.getTitle());
            textView.setTextSize(15);
            textView.setTypeface(bold);
            textView.setTextColor(getResources().getColor(R.color.white));

            contentLayout.addView(textView);

            ImageView separator = new ImageView(getContext());
            separator.setImageResource(R.drawable.right_arrow);

            contentLayout.addView(separator);

        }


        BreadcrumbEvent lastEvent = elements.get(elements.size() - 1);
        TextView lastTextView = new TextView(getContext());

        lastTextView.setText(lastEvent.getTitle());
        lastTextView.setTextSize(15);
        lastTextView.setTypeface(bold);
        lastTextView.setTextColor(getResources().getColor(R.color.white));
        contentLayout.addView(lastTextView);



        return view;
    }
}