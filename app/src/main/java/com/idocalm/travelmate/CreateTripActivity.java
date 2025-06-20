package com.idocalm.travelmate;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.enums.TripVisibility;
import com.idocalm.travelmate.models.Flight;
import com.idocalm.travelmate.models.Hotel;
import com.idocalm.travelmate.models.ItineraryActivity;
import com.idocalm.travelmate.models.Trip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CreateTripActivity extends AppCompatActivity {

    EditText destinationEditText, startDateEditText, endDateEditText, tripNameEditText, tripDescEditText;
    LinearLayout participantsLayout;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_trip);

        participantsLayout = findViewById(R.id.trip_participants_panel);
        participantsLayout.setVisibility(View.GONE);

        destinationEditText = findViewById(R.id.trip_dest);
        startDateEditText = findViewById(R.id.trip_start_date);
        endDateEditText = findViewById(R.id.trip_end_date);
        tripNameEditText = findViewById(R.id.trip_name);
        tripDescEditText = findViewById(R.id.trip_desc);

        // Disable keyboard input and open date picker
        startDateEditText.setInputType(0);
        endDateEditText.setInputType(0);

        startDateEditText.setOnClickListener(v -> showDatePicker(startDateEditText));
        endDateEditText.setOnClickListener(v -> showDatePicker(endDateEditText));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int tripAmount = extras.getInt("tripAmount");
            tripNameEditText.setText("Trip #" + (tripAmount + 1));
        } else {
            tripNameEditText.setText("Trip #1");
        }

        Button create = findViewById(R.id.create_trip_button);
        create.setOnClickListener(v -> {
            boolean valid = true;

            if (tripNameEditText.getText().toString().isEmpty()) {
                tripNameEditText.setError("Please enter a trip name");
                valid = false;
            }

            if (destinationEditText.getText().toString().isEmpty()) {
                destinationEditText.setError("Please enter a destination");
                valid = false;
            }

            if (tripDescEditText.getText().toString().isEmpty()) {
                tripDescEditText.setError("Please enter a description");
                valid = false;
            }

            if (startDateEditText.getText().toString().isEmpty() || !validateDate(startDateEditText.getText().toString())) {
                startDateEditText.setError("Please enter a valid start date");
                valid = false;
            } else if (endDateEditText.getText().toString().isEmpty() || !validateDate(endDateEditText.getText().toString())) {
                endDateEditText.setError("Please enter a valid end date");
                valid = false;
            } else {
                if (isPast(startDateEditText.getText().toString())) {
                    startDateEditText.setError("Start date can't be in the past");
                    valid = false;
                } else if (!isBefore(startDateEditText.getText().toString(), endDateEditText.getText().toString())) {
                    endDateEditText.setError("End date must be after start date");
                    valid = false;
                }
            }

            if (valid) {
                String name = tripNameEditText.getText().toString();
                String destination = destinationEditText.getText().toString();
                String description = tripDescEditText.getText().toString();
                String image = "";

                try {
                    Date startDateParsed = sdf.parse(startDateEditText.getText().toString());
                    Date endDateParsed = sdf.parse(endDateEditText.getText().toString());

                    if (startDateParsed == null || endDateParsed == null) {
                        Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Timestamp start = new Timestamp(startDateParsed);
                    Timestamp end = new Timestamp(endDateParsed);

                    if (start.toDate().after(end.toDate())) {
                        Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (end.toDate().before(new Date())) {
                        Toast.makeText(this, "End date can't be in the past", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (start.toDate().before(new Date())) {
                        Toast.makeText(this, "Start date can't be in the past", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArrayList<String> members = new ArrayList<>();
                    members.add(Auth.getUser().getId());

                    ArrayList<ItineraryActivity> activities = new ArrayList<>();
                    ArrayList<Hotel> hotels = new ArrayList<>();
                    ArrayList<Flight> flights = new ArrayList<>();

                    Trip trip = new Trip(
                            null, name, destination, Auth.getUser().getId(), description, image, members,
                            start, end, new Timestamp(new Date()), new Timestamp(new Date()), new Timestamp(new Date()),
                            activities, hotels, flights
                    );

                    HashMap<String, Object> t = Trip.toHashMap(trip);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("trips").add(t).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String id = task.getResult().getId();
                            db.collection("trips").document(id).update("id", id);
                            Auth.getUser().addTripId(id);
                            Toast.makeText(this, "Trip created successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (ParseException e) {
                    Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDatePicker(EditText targetEditText) {
        Calendar calendar = Calendar.getInstance();

        // Pre-fill picker if date is valid
        String dateStr = targetEditText.getText().toString();
        if (validateDate(dateStr)) {
            try {
                Date date = sdf.parse(dateStr);
                calendar.setTime(date);
            } catch (ParseException ignored) {}
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);
                    targetEditText.setText(sdf.format(selectedDate.getTime()));
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private boolean validateDate(String string) {
        return string.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})");
    }

    private boolean isBefore(String start, String end) {
        String[] startParts = start.split("/");
        String[] endParts = end.split("/");

        Calendar startDate = Calendar.getInstance();
        startDate.set(Integer.parseInt(startParts[2]), Integer.parseInt(startParts[1]) - 1, Integer.parseInt(startParts[0]));

        Calendar endDate = Calendar.getInstance();
        endDate.set(Integer.parseInt(endParts[2]), Integer.parseInt(endParts[1]) - 1, Integer.parseInt(endParts[0]));

        return startDate.before(endDate);
    }

    private boolean isPast(String date) {
        String[] parts = date.split("/");

        Calendar d = Calendar.getInstance();
        d.set(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[0]));

        return d.before(Calendar.getInstance());
    }
}
