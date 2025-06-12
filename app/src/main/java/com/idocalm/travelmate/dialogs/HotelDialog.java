package com.idocalm.travelmate.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.models.Hotel;
import com.idocalm.travelmate.models.Trip;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HotelDialog extends Dialog {

    Button bookingButton, addButton;

    Hotel hotel;
    JSONObject hotelData;

    public HotelDialog(Activity a, Hotel hotel, JSONObject hotelData) {
        super(a);
        this.hotel = hotel;
        this.hotelData = hotelData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.hotel_dialog);

        bookingButton = findViewById(R.id.hotel_dialog_booking);
        addButton = findViewById(R.id.hotel_dialog_add);
        ImageView hotelImage = findViewById(R.id.dialog_hotel_image);

        Glide.with(getContext())
                .load(hotel.getMainPhoto())
                .into(hotelImage);

        bookingButton.setOnClickListener((v) -> {
            try {
                String url = hotelData.getString("url");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                getContext().startActivity(intent);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


        });

        addButton.setOnClickListener((v) -> {
            ArrayList<String> tripIds = Auth.getUser().getTripIds();

            if (tripIds.isEmpty()) {
                Toast.makeText(getContext(), "You need to create a trip first", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                HashMap<String, String> trips = new HashMap<>();
                for (String tripId : tripIds) {
                    db.collection("trips").document(tripId).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            trips.put(tripId, task1.getResult().getString("name"));

                            if (trips.size() == tripIds.size()) {
                                Log.d("HotelDialog", "All trips fetched: " + trips.size());

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select a trip");
                                String[] tripNames = new String[trips.size()];

                                int i = 0;
                                for (String id : trips.keySet()) {
                                    tripNames[i] = trips.get(id);
                                    i++;
                                }



                                builder.setItems(tripNames, (dialog, which) -> {
                                    String selectedTripName = tripNames[which];
                                    String selectedTripId = null;

                                    for (String id : trips.keySet()) {
                                        if (trips.get(id).equals(selectedTripName)) {
                                            selectedTripId = id;
                                            break;
                                        }
                                    }

                                    String finalSelectedTripId = selectedTripId;
                                    Auth.getUser().getTrip(selectedTripId, new Trip.TripCallback() {
                                        @Override
                                        public void onTripLoaded(Trip t) {
                                            if (t.getOwner() != null && !t.getOwner().equals(Auth.getUser().getId())) {
                                                Toast.makeText(getContext(), "You can only add hotels to your own trips", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.d("Adding hotel", "Trip ID: " + finalSelectedTripId);
                                                HashMap<String, Object> hotelMap = Hotel.toHashMap(hotel);
                                                ;
                                                db.collection("trips").document(finalSelectedTripId).collection("hotels").add(hotelMap)
                                                        .addOnSuccessListener(documentReference -> {
                                                            Toast.makeText(getContext(), "Hotel added to trip", Toast.LENGTH_SHORT).show();
                                                            dismiss();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(getContext(), "Error adding hotel to trip", Toast.LENGTH_SHORT).show();
                                                        });
                                            }
                                        }

                                        @Override
                                        public void onError(Exception e) {

                                        }
                                    });


                                });

                                builder.setNegativeButton("Cancel", (dialog, which) -> {
                                    dialog.dismiss();
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                }
            }
        });

        TextView name = findViewById(R.id.dialog_hotel_name);
        name.setText(hotel.getName());

    }


}