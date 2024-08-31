package com.idocalm.travelmate;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.fragments.ExploreFragment;
import com.idocalm.travelmate.fragments.HomeFragment;
import com.idocalm.travelmate.fragments.ProfileFragment;
import com.idocalm.travelmate.fragments.SearchFragment;
import com.idocalm.travelmate.tutorials.TripTutorial;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView navbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navbar = findViewById(R.id.navbar);
        navbar.getMenu().getItem(2).setEnabled(false);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        navbar.setSelectedItemId(R.id.home);

        navbar.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            } else if (item.getItemId() == R.id.explore) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ExploreFragment()).commit();
            } else if (item.getItemId() == R.id.profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            } else if (item.getItemId() == R.id.search) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
            }
            return true;
        });

        FloatingActionButton fab = findViewById(R.id.trip_button);
        fab.setOnClickListener(view -> {
            if (!Auth.getUser().getTripIds().isEmpty()) {
                Intent intent = new Intent(HomeActivity.this, TripTutorial.class);
                startActivity(intent);
            }
        });

    }
}