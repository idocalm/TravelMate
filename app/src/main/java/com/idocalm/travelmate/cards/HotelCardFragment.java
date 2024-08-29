package com.idocalm.travelmate.cards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.dialogs.HotelDialog;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HotelCardFragment extends Fragment {


    private String name;
    private String mainPhoto;
    private double latitude;
    private double longitude;


    public HotelCardFragment(String name, String mainPhoto, double latitude, double longitude) {
        this.name = name;
        this.mainPhoto = mainPhoto;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /* TODO: This whole class might be un necessary */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hotel_card, container, false);


        return view;
    }
}