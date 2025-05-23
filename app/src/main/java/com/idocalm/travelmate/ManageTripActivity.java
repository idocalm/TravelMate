package com.idocalm.travelmate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.components.ActivitiesExpandableAdapter;
import com.idocalm.travelmate.components.explore.HotelsListAdapter;
import com.idocalm.travelmate.models.ItineraryActivity;
import com.idocalm.travelmate.models.Trip;
import com.idocalm.travelmate.models.User;

import org.w3c.dom.Text;

import java.sql.Array;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.view.ViewGroup;

public class ManageTripActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_trip);

        EditText name = findViewById(R.id.trip_name);
        ExpandableListView expandableListView = findViewById(R.id.expandable_activities);
        Button createActivity2 = findViewById(R.id.create_activity_2);
        ImageView createActivity = findViewById(R.id.create_activity);

        String id = getIntent().getStringExtra("trip_id");

        LinearLayout noActivities = findViewById(R.id.no_activities);
        TextView noHotels = findViewById(R.id.trip_no_hotels);
        ListView hotelList = findViewById(R.id.trip_hotels);


        ImageButton inviteFriend = findViewById(R.id.add_friend_totrip);

        // when clicking the invite button, show a simple dialog with each friend and a checkbox, then button "apply"
        inviteFriend.setOnClickListener(v -> {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.invite_friends_dialog);
            dialog.setTitle("Invite Friends");

            ListView friendsList = dialog.findViewById(R.id.friends_list);
            Button apply = dialog.findViewById(R.id.apply_invite);

            // Prepare data
            ArrayList<String> friendsId = Auth.getUser().getFriendsIds();
            ArrayList<String> friendNames = new ArrayList<>();

            // Adapter for showing names with checkboxes
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, friendNames);
            friendsList.setAdapter(adapter);
            friendsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            // Fetch friend names from Firestore
            for (String friendId : friendsId) {
                FirebaseFirestore.getInstance().collection("users").document(friendId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            User user = User.fromDocument(documentSnapshot);
                            if (user != null) {
                                friendNames.add(user.getName());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
            }

            apply.setOnClickListener(v1 -> {
                SparseBooleanArray checked = friendsList.getCheckedItemPositions();
                ArrayList<String> selectedFriendIds = new ArrayList<>();
                for (int i = 0; i < friendNames.size(); i++) {
                    if (checked.get(i)) {
                        // Map name back to ID
                        selectedFriendIds.add(friendsId.get(i));
                    }
                }

                // Example: Add selected friends to the trip's invitedFriends field
                if (!selectedFriendIds.isEmpty()) {
                    FirebaseFirestore.getInstance().collection("trips").document(id)
                        .update("invitedFriends", selectedFriendIds)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ManageTripActivity.this, "Friends invited successfully!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ManageTripActivity.this, "Failed to invite friends", Toast.LENGTH_SHORT).show();
                        });
                }
                dialog.dismiss();
            });

            dialog.show();
        });



        FirebaseFirestore db = FirebaseFirestore.getInstance();


        name.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                db.collection("trips").document(id).update("name", s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        Auth.getUser().getTrip(id, new Trip.TripCallback() {
            @Override
            public void onTripLoaded(Trip t) {
                name.setText(t.getName());

                List<String> dateList = new ArrayList<>();
                HashMap<String, List<ItineraryActivity>> map = new HashMap<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                for (Map<String, Object> actMap : t.getActivities()) {
                    ItineraryActivity act = ItineraryActivity.fromMap(actMap);
                    String dateStr = sdf.format(act.getDate().toDate());

                    map.computeIfAbsent(dateStr, k -> {
                        dateList.add(k);
                        return new ArrayList<>();
                    });
                    map.get(dateStr).add(act);
                }

                if (map.isEmpty()) {
                    noActivities.setVisibility(View.VISIBLE);
                } else {
                    noActivities.setVisibility(View.GONE);
                }

                // sort the dates
                dateList.sort((d1, d2) -> {
                    try {
                        Date date1 = sdf.parse(d1);
                        Date date2 = sdf.parse(d2);
                        return date1.compareTo(date2);
                    } catch (Exception e) {
                        return 0;
                    }
                });



                ActivitiesExpandableAdapter adapter = new ActivitiesExpandableAdapter(ManageTripActivity.this, ManageTripActivity.this, t, dateList, map);
                expandableListView.setAdapter(adapter);


                createActivity.setOnClickListener(v -> {
                    newActivityPopup(t, id, () -> {
                        recreate();
                    });
                });

                createActivity2.setOnClickListener(v -> {
                    newActivityPopup(t, id, () -> {
                        recreate();
                    });
                });

                if (t.getHotels() == null || t.getHotels().isEmpty()) {
                    noHotels.setVisibility(View.VISIBLE);
                    hotelList.setVisibility(View.GONE);
                } else {
                    noHotels.setVisibility(View.GONE);
                    hotelList.setVisibility(View.VISIBLE);

                    HotelsListAdapter adap = new HotelsListAdapter(ManageTripActivity.this, t.getHotels(), true, id);
                    hotelList.setAdapter(adap);
                }

                ImageView tripImage = findViewById(R.id.trip_image);


                Glide.with(ManageTripActivity.this)
                        .load(t.getImage())
                        .placeholder(R.drawable.trip_placeholder)
                        .into(tripImage);

                tripImage.setOnClickListener(v -> {

                    AlertDialog.Builder builder = new AlertDialog.Builder( ManageTripActivity.this);
                    builder.setTitle("Change Image");

                    builder.setMessage("Enter the URL of the image you want to use");
                    EditText input = new EditText(ManageTripActivity.this);
                    builder.setView(input);

                    builder.setPositiveButton("Change", (dialogInterface, i) -> {
                        String url = input.getText().toString();
                        Glide.with(ManageTripActivity.this)
                                .load(url)
                                .placeholder(R.drawable.trip_placeholder)
                                .error(R.drawable.trip_placeholder)
                                .into((ImageView) findViewById(R.id.trip_image));
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        db.collection("trips").document(getIntent().getStringExtra("trip_id")).update("image", url);

                    });

                    builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    });

                    builder.show();

                });


            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ManageTripActivity.this, "Trip not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }

    public void newActivityPopup(Trip trip, String id, Runnable onActivityAdded) {
        Dialog dialog = new Dialog(this);

        // change dialog width
        AtomicReference<Boolean> noteOpen = new AtomicReference<>(false);
        AtomicReference<Boolean> costOpen = new AtomicReference<>(false);

        dialog.setContentView(R.layout.add_trip_activity_dialog);
        dialog.findViewById(R.id.activity_note).setVisibility(LinearLayout.GONE);
        dialog.findViewById(R.id.activity_cost).setVisibility(LinearLayout.GONE);
        Button toggleNote = dialog.findViewById(R.id.add_activity_note);
        Button toggleCost = dialog.findViewById(R.id.add_activity_cost);

        toggleNote.setText("Add Note");
        toggleCost.setText("Add Cost");

        toggleNote.setOnClickListener(v1 -> {
            if (noteOpen.get()) {
                dialog.findViewById(R.id.activity_note).setVisibility(LinearLayout.GONE);
                toggleNote.setText("Add Note");
                noteOpen.set(false);
            } else {
                dialog.findViewById(R.id.activity_note).setVisibility(LinearLayout.VISIBLE);
                toggleNote.setText("Del. Note");
                noteOpen.set(true);
            }
        });

        toggleCost.setOnClickListener(v1 -> {
            if (costOpen.get()) {
                dialog.findViewById(R.id.activity_cost).setVisibility(LinearLayout.GONE);
                toggleCost.setText("Add Cost");
                costOpen.set(false);
            } else {
                dialog.findViewById(R.id.activity_cost).setVisibility(LinearLayout.VISIBLE);
                toggleCost.setText("Del. Cost");
                costOpen.set(true);
            }
        });

        Button save = dialog.findViewById(R.id.submit_activity);
        save.setOnClickListener(v1 -> {
            // get the values from the dialog
            String activityName = ((EditText) dialog.findViewById(R.id.activity_name)).getText().toString();
            String location = ((EditText) dialog.findViewById(R.id.activity_location)).getText().toString();
            String note = ((EditText) dialog.findViewById(R.id.activity_note)).getText().toString();
            String cost = ((EditText) dialog.findViewById(R.id.activity_cost)).getText().toString();

            if (activityName.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Activity Name and Location are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (cost.isEmpty()) {
                cost = "0";
            }

            if (note.isEmpty()) {
                note = "";
            }


            EditText dateField = dialog.findViewById(R.id.activity_date);
            if (dateField.getText().toString().isEmpty()) {
                Toast.makeText(this, "Date is required", Toast.LENGTH_SHORT).show();
                return;
            }

            DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date;
            try {
                date = df.parse(dateField.getText().toString());
            } catch (Exception e) {
                Toast.makeText(this, "Invalid date format (d/m/y)", Toast.LENGTH_SHORT).show();
                return;
            }

            // combine date and time to a timestamp
            String timeStr = ((EditText) dialog.findViewById(R.id.activity_time)).getText().toString();
            Time time;
            try {
                time = Time.valueOf(timeStr + ":00");
            } catch (Exception e) {
                Toast.makeText(this, "Invalid time format (hour:minute)", Toast.LENGTH_SHORT).show();
                return;
            }

            date.setHours(time.getHours());
            date.setMinutes(time.getMinutes());
            date.setSeconds(0);
            Timestamp timestamp = new Timestamp(date);

            ItineraryActivity activity = new ItineraryActivity(activityName, location, timestamp, note, cost);
            trip.addActivity(activity);
            onActivityAdded.run();



            dialog.dismiss();
        });

        dialog.show();
    }


}