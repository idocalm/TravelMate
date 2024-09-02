package com.idocalm.travelmate.tutorials;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.idocalm.travelmate.CreateTripActivity;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.components.triptutorial.TripTutorialAdapter;

public class TripTutorial extends AppCompatActivity {

    ViewPager viewPager;

    Button back, next, skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trip_tutorial);

        viewPager = findViewById(R.id.tutorial_view_pager);
        back = findViewById(R.id.back_button);
        next = findViewById(R.id.next_button);
        skip = findViewById(R.id.skip_button);

        TripTutorialAdapter adapter = new TripTutorialAdapter();
        viewPager.setAdapter(adapter);

        back.setOnClickListener(view -> {
            int current = viewPager.getCurrentItem();
            if (current > 0) {
                viewPager.setCurrentItem(current - 1);
            }
        });

        next.setOnClickListener(view -> {
            int current = viewPager.getCurrentItem();
            if (current < adapter.getCount() - 1) {
                viewPager.setCurrentItem(current + 1);
            } else {
                Intent intent = new Intent(TripTutorial.this, CreateTripActivity.class);
                startActivity(intent);
                finish();
            }
        });

        skip.setOnClickListener(view -> {
            finish();
        });

    }
}