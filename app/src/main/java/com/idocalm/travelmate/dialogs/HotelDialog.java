package com.idocalm.travelmate.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.idocalm.travelmate.components.explore.HotelsSearchFragment;
import com.idocalm.travelmate.R;

import org.json.JSONException;
import org.json.JSONObject;

public class HotelDialog extends Dialog {

    Button bookingButton, doneButton;

    JSONObject hotelData;
    String name;
    double latitude;
    double longitude;
    String mainPhoto;


    public HotelDialog(Activity a, String name, double latitude, double longitude, String mainPhoto, JSONObject hotelData) {
        super(a);
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mainPhoto = mainPhoto;
        this.hotelData = hotelData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.hotel_dialog);

        bookingButton = findViewById(R.id.hotel_dialog_booking);
        doneButton = findViewById(R.id.hotel_dialog_done);

        bookingButton.setOnClickListener((v) -> {
            try {
                String url = hotelData.getString("url");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                getContext().startActivity(intent);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


        });

        doneButton.setOnClickListener((v) -> {
            dismiss();
        });

        TextView name = findViewById(R.id.dialog_hotel_name);
        name.setText(this.name);

        Geocoder geocoder = new Geocoder(getContext());
        TextView location = findViewById(R.id.dialog_hotel_address);
        try {
            location.setText(geocoder.getFromLocation(latitude, longitude, 1).get(0).getAddressLine(0));
        } catch (Exception e) {
            location.setText("Not available");
        }



    }


}