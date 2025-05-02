package com.idocalm.travelmate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.components.ActivitiesExpandableAdapter;
import com.idocalm.travelmate.models.ItineraryActivity;
import com.idocalm.travelmate.models.Trip;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        name.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                db.collection("trips").document(id).update("name", s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        db.collection("trips").document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Trip t = Trip.fromDB(task.getResult());
                name.setText(t.getName());

                createActivity.setOnClickListener(v -> {
                    newActivityPopup(t, id);
                });

                createActivity2.setOnClickListener(v -> {
                    newActivityPopup(t, id);
                });

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

                ActivitiesExpandableAdapter adapter = new ActivitiesExpandableAdapter(this, dateList, map);
                expandableListView.setAdapter(adapter);

                Glide.with(this)
                        .load(t.getImage())
                        .placeholder(R.drawable.trip_placeholder)
                        .into((ImageView) findViewById(R.id.trip_image));
            }
        });
    }

    public void newActivityPopup(Trip trip, String id) {
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


            // save the activity to the database
            Timestamp timestamp = new Timestamp(new Date());

            ItineraryActivity activity = new ItineraryActivity(activityName, location, timestamp, Long.parseLong("200"), note, cost);

            trip.addActivity(activity);
            Log.d("Activities", trip.getActivities().toString());

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("trips").document(id).update("itinerary", trip.getActivities());


            dialog.dismiss();
        });

        dialog.show();
    }


}