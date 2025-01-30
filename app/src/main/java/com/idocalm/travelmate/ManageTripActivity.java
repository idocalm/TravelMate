package com.idocalm.travelmate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.models.ItineraryActivity;
import com.idocalm.travelmate.models.Trip;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.DateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ManageTripActivity extends AppCompatActivity {


    public void setImage(Bitmap image) {
        runOnUiThread( new Runnable() {
            @Override
            public void run() {
                ImageView imageView = findViewById(R.id.trip_image);
                imageView.setImageBitmap(image);
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_trip);

        EditText name = findViewById(R.id.trip_name);

        // on name change save the new name to the database
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("trips").document(getIntent().getStringExtra("trip_id")).update("name", name.getText().toString());

            }


            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        ImageView image = findViewById(R.id.trip_image);
        image.setOnClickListener(v -> {
            // open a dialog with an option to enter a URL to change the image,
            // you should use the default dialog builder

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Change Image");

            builder.setMessage("Enter the URL of the image you want to use");
            EditText input = new EditText(this);
            builder.setView(input);

            builder.setPositiveButton("Change", (dialogInterface, i) -> {
                // get the URL from the input
                String url = input.getText().toString();
                // set the image to the new URL
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL imageUrl = new URL(url);
                            HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                            connection.setDoInput(true);
                            connection.connect();
                            InputStream input = connection.getInputStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(input);
                            setImage(bitmap);

                            // save the new image URL to the database
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("trips").document(getIntent().getStringExtra("trip_id")).update("image", url);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            });

            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });

            builder.show();

        });

        LinearLayout activities = findViewById(R.id.activities_list);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // get the trip from the intent
        String id = getIntent().getStringExtra("trip_id");

        db.collection("trips").document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Trip trip = Trip.fromDB(task.getResult());
                for (Map<String, Object> activity : trip.getActivities()) {
                    Log.d("Activity", activity.toString());
                    // create a new view for the activity
                    LinearLayout activityView = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_small_card, null);
                    ((TextView) activityView.findViewById(R.id.activity_card_name)).setText((String) activity.get("name"));
                    ((TextView) activityView.findViewById(R.id.activity_card_location)).setText((String) activity.get("location"));
                    // format date to dd/MM/yyyy
                    DateFormat dateFormat = DateFormat.getDateInstance();
                    String formatDate = dateFormat.format(((Timestamp) activity.get("start_date")).toDate());

                    ((TextView) activityView.findViewById(R.id.activity_card_date)).setText(formatDate);
                    activities.addView(activityView);
                }


                Button addActivity = findViewById(R.id.start_here);


                addActivity.setOnClickListener(v -> {
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

                        db.collection("trips").document(id).update("itinerary", trip.getActivities());


                        dialog.dismiss();
                    });

                    dialog.show();
                });


                Toast.makeText(this, "Trip ID: " + id, Toast.LENGTH_SHORT).show();

                // get the trip from the database
                db.collection("trips").document(id).get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        name.setText(trip.getName());

                        LinearLayout noActivities = findViewById(R.id.no_activities);

                        if (trip.getActivities().isEmpty()) {
                            noActivities.setVisibility(LinearLayout.VISIBLE);
                        } else {
                            noActivities.setVisibility(LinearLayout.GONE);
                        }

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    URL url = new URL(trip.getImage());
                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                    connection.setDoInput(true);
                                    connection.connect();
                                    InputStream input = connection.getInputStream();
                                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                                    setImage(bitmap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                        thread.start();

                    }
                });

            }


        });

    }
}