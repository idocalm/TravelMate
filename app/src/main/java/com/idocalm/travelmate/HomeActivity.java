package com.idocalm.travelmate;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView navbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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
            }
            return true;
        });
    }
}