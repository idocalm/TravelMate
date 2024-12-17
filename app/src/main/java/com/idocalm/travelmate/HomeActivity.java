package com.idocalm.travelmate;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.fragments.BreadcrumbFragment;
import com.idocalm.travelmate.fragments.ExploreFragment;
import com.idocalm.travelmate.fragments.HomeFragment;
import com.idocalm.travelmate.fragments.ProfileAccountFragment;
import com.idocalm.travelmate.fragments.ProfileContentFragment;
import com.idocalm.travelmate.fragments.ProfileSettingsFragment;
import com.idocalm.travelmate.fragments.SearchFragment;
import com.idocalm.travelmate.models.BreadcrumbEvent;
import com.idocalm.travelmate.tutorials.TripTutorial;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements ProfileContentFragment.ContentListener {

    BottomNavigationView navbar;
    BreadcrumbFragment breadcrumbFragment;

    Runnable dummy = () -> {};
    BreadcrumbEvent profile = new BreadcrumbEvent("Profile", dummy);

    BreadcrumbFragment getEmptyBreadcrumb() {
        ArrayList<BreadcrumbEvent> defaults = new ArrayList<>();
        defaults.add(profile);
        return new BreadcrumbFragment(defaults, new ProfileContentFragment());
    }

    @Override
    public void onAccountClick() {

        BreadcrumbEvent account = new BreadcrumbEvent("Account", dummy);
        ArrayList<BreadcrumbEvent> accountList = new ArrayList<>();
        accountList.add(profile);
        accountList.add(account);
        breadcrumbFragment = new BreadcrumbFragment(accountList, new ProfileAccountFragment(

        ));
        breadcrumbFragment.onHomeClick = () -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, getEmptyBreadcrumb()).commit();
        };
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, breadcrumbFragment).commit();
    }

    @Override
    public void onSettingsClick() {
        BreadcrumbEvent settings = new BreadcrumbEvent("Settings", dummy);
        ArrayList<BreadcrumbEvent> settingsList = new ArrayList<>();
        settingsList.add(profile);
        settingsList.add(settings);
        breadcrumbFragment = new BreadcrumbFragment(settingsList, new ProfileSettingsFragment());
        breadcrumbFragment.onHomeClick = () -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, getEmptyBreadcrumb()).commit();
        };
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, breadcrumbFragment).commit();
    }


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
                ArrayList<BreadcrumbEvent> defaults = new ArrayList<>();
                defaults.add(profile);
                breadcrumbFragment = new BreadcrumbFragment(defaults, new ProfileContentFragment());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, breadcrumbFragment).commit();
            } else if (item.getItemId() == R.id.search) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
            }
            return true;
        });

        FloatingActionButton fab = findViewById(R.id.trip_button);
        fab.setOnClickListener(view -> {
            if (Auth.getUser().getTripIds().isEmpty()) {
                Intent intent = new Intent(HomeActivity.this, TripTutorial.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(HomeActivity.this, CreateTripActivity.class);
                intent.putExtra("tripAmount", Auth.getUser().getTripIds().size());
                startActivity(intent);
            }
        });

    }
}