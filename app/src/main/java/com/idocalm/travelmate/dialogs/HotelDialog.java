package com.idocalm.travelmate.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.idocalm.travelmate.components.explore.HotelsSearchFragment;
import com.idocalm.travelmate.R;

public class HotelDialog extends Dialog {

    Button bookingButton, doneButton;


    public HotelDialog(Activity a, String name, double latitude, double longitude, String mainPhoto) {
        super(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.hotel_dialog);

        bookingButton = findViewById(R.id.hotel_dialog_booking);
        doneButton = findViewById(R.id.hotel_dialog_done);

        bookingButton.setOnClickListener((v) -> {

        });

        doneButton.setOnClickListener((v) -> {
            dismiss();
        });



    }


}