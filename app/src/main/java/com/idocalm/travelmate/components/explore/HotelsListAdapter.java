package com.idocalm.travelmate.components.explore;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.idocalm.travelmate.R;
import com.idocalm.travelmate.api.Hotels;
import com.idocalm.travelmate.dialogs.HotelDialog;
import com.idocalm.travelmate.models.Hotel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HotelsListAdapter extends ArrayAdapter<Hotel> {

    public HotelsListAdapter(Context context, ArrayList<Hotel> hotels) {
        super(context, 0, hotels);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;


        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_hotel_card, parent, false);
        }

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
            HotelDialog dialog = new HotelDialog(activity, hotel.getName(), hotel.getLatitude(), hotel.getLongitude(), hotel.getMainPhoto(), hotelData);
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
