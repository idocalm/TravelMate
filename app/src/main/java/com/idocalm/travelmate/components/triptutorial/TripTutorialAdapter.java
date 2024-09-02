package com.idocalm.travelmate.components.triptutorial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.idocalm.travelmate.R;

public class TripTutorialAdapter extends PagerAdapter {

    String[] headings = {
            "Let's plan on your first trip!",
            "Name, destination & dates",
            "Add your travel buddies",
            "That's it!",
    };

    String[] descriptions = {
            "",
            "Start by choosing a destination for your trip, and enter your trip dates.",
            "Add your travel buddies to your trip. You can add them by their username.",
            "You're all set! You can now start planning your next trip!",
    };

    @Override
    public int getCount() {
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view) == (LinearLayout)  object;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View view = inflater.inflate(R.layout.fragment_trip_tutorial_card, container, false);

        TextView heading = view.findViewById(R.id.tutorial_title);
        TextView description = view.findViewById(R.id.tutorial_description);

        heading.setText(headings[position]);
        description.setText(descriptions[position]);


        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }

}
