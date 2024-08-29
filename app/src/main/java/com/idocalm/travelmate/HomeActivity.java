package com.idocalm.travelmate;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.idocalm.travelmate.fragments.ExploreFragment;
import com.idocalm.travelmate.fragments.HomeFragment;
import com.idocalm.travelmate.fragments.ProfileFragment;
import com.idocalm.travelmate.fragments.SearchFragment;

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

    }
}