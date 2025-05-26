package com.idocalm.travelmate.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.idocalm.travelmate.R;
import com.idocalm.travelmate.api.Hotels;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.dialogs.HotelDialog;
import com.idocalm.travelmate.models.Hotel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HotelsListAdapter extends ArrayAdapter<Hotel> {

    private boolean isTripView;
    private String tripId;

    public HotelsListAdapter(Context context, ArrayList<Hotel> hotels, boolean isTripView, String tripId) {
        super(context, 0, hotels);

        this.isTripView = isTripView;
        this.tripId = tripId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (isTripView)
            return setupTripView(parent, position);

        return setupNormalView(parent, position);

    }


    private View setupTripView(ViewGroup parent, int position) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_trip_hotel_card, parent, false);

        Hotel hotel = getItem(position);

        TextView hotelNameTextView = view.findViewById(R.id.hotel_name);
        hotelNameTextView.setText(hotel.getName());

        TextView hotelPriceTextView = view.findViewById(R.id.hotel_price);
        String price = Auth.getUser().getCurrencyString() + " " + hotel.getPrice() + " / night";
        hotelPriceTextView.setText(price);

        TextView hotelDatesTextView = view.findViewById(R.id.hotel_dates);
        String checkInDate = new SimpleDateFormat("dd/MM/yyyy").format(hotel.getCheckInDate());
        String checkOutDate = new SimpleDateFormat("dd/MM/yyyy").format(hotel.getCheckOutDate());
        hotelDatesTextView.setText(checkInDate + " - " + checkOutDate);

        LinearLayout delete = view.findViewById(R.id.delete_hotel);

        delete.setOnClickListener((v) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Delete Hotel");
            builder.setMessage("Are you sure you want to delete this hotel?");
            builder.setPositiveButton("Delete", (dialog, which) -> {
                dialog.dismiss();
                
                Hotel.deleteHotel(tripId, hotel.getDBId());
                Toast.makeText(getContext(), "Hotel deleted", Toast.LENGTH_SHORT).show();
                ((Activity) getContext()).recreate();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        });

        return view;

    }

    private View setupNormalView(ViewGroup parent, int position) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_hotel_card, parent, false);

        view.setOnClickListener((v) -> {
            Toast.makeText(getContext(), "Long press to view more details", Toast.LENGTH_SHORT).show();
        });

        Hotel hotel = getItem(position);

        TextView hotelNameTextView = view.findViewById(R.id.hotel_name);


        String formattedName = hotel.getName().substring(0, Math.min(hotel.getName().length(), 40));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < formattedName.length(); i++) {
            sb.append(formattedName.charAt(i));
            if (i % 15 == 0 && i != 0) {
                sb.append("\n");
            }
        }

        hotelNameTextView.setText(sb.toString());

        view.setOnLongClickListener((v) -> {

            try {
                openHotelDialog(hotel);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            return true;
        });

        return view;
    }

    private void openDialog(Hotel hotel, JSONObject hotelData) {

        Activity activity = (Activity) getContext();
        activity.runOnUiThread(() -> {
            HotelDialog dialog = new HotelDialog(activity, hotel, hotelData);
            dialog.show();
        });
    }

    private void openHotelDialog(Hotel hotel) throws JSONException, IOException {


        /* get  activity */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject hotelData = Hotels.getHotelData(hotel);
                    openDialog(hotel, hotelData);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }

}
