package com.idocalm.travelmate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.adapters.ActivitiesExpandableAdapter;
import com.idocalm.travelmate.adapters.FlightsListAdapter;
import com.idocalm.travelmate.adapters.HotelsListAdapter;
import com.idocalm.travelmate.enums.CurrencyType;
import com.idocalm.travelmate.models.ItineraryActivity;
import com.idocalm.travelmate.models.Trip;
import com.idocalm.travelmate.models.User;
import com.idocalm.travelmate.adapters.TripMembersAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.util.Calendar;

public class ManageTripActivity extends AppCompatActivity {
    private TripMembersAdapter membersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_trip);

        EditText name = findViewById(R.id.trip_name);
        ExpandableListView expandableListView = findViewById(R.id.expandable_activities);
        Button createActivity2 = findViewById(R.id.create_activity_2);
        ImageView createActivity = findViewById(R.id.create_activity);
        ImageButton inviteFriend = findViewById(R.id.add_friend_totrip);

        String id = getIntent().getStringExtra("trip_id");
        boolean isMember = getIntent().getBooleanExtra("is_member", false);

        LinearLayout noActivities = findViewById(R.id.no_activities);
        TextView noHotels = findViewById(R.id.trip_no_hotels);
        ListView hotelList = findViewById(R.id.trip_hotels);
        ListView flightList = findViewById(R.id.trip_flights);

        // Disable editing capabilities for members
        if (isMember) {
            name.setEnabled(false);
            createActivity.setVisibility(View.GONE);
            createActivity2.setVisibility(View.GONE);
            inviteFriend.setVisibility(View.GONE);
        }

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

                // Add selected friends to the trip's members field
                if (!selectedFriendIds.isEmpty()) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("trips").document(id).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Trip.fromDB(documentSnapshot, new Trip.TripCallback() {
                                    @Override
                                    public void onTripLoaded(Trip trip) {
                                        for (String friendId : selectedFriendIds) {
                                            trip.addMember(friendId);

                                            // Add trip ID to friend's tripIds
                                            db.collection("users").document(friendId).get()
                                                    .addOnSuccessListener(userDoc -> {
                                                        if (userDoc.exists()) {
                                                            User friend = User.fromDocument(userDoc);
                                                            if (friend != null) {
                                                                friend.addTripId(id);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                    @Override
                                    public void onError(Exception e) {
                                        Toast.makeText(ManageTripActivity.this, "Failed to add friends", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ManageTripActivity.this, "Failed to add friends", Toast.LENGTH_SHORT).show();
                        });
                }
                dialog.dismiss();
            });

            dialog.show();
        });

        RecyclerView tripMembers = findViewById(R.id.trip_members);
        membersAdapter = new TripMembersAdapter(this);
        tripMembers.setAdapter(membersAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Auth.getUser().getTrip(id, new Trip.TripCallback() {
            @Override
            public void onTripLoaded(Trip t) {
                name.setText(t.getName());

                // Only allow name editing for trip owner
                if (!isMember) {
                    name.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}

                        @Override
                        public void afterTextChanged(Editable s) {
                            t.setName(s.toString());
                            t.save();
                        }
                    });
                }

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

                if (!isMember) {
                    createActivity.setOnClickListener(v -> {
                        newActivityPopup(t, id, () -> {
                            recreate();
                        });
                    });
                }

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

                if (t.getFlights() == null || t.getFlights().isEmpty()) {
                    findViewById(R.id.trip_no_flights).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.trip_no_flights).setVisibility(View.GONE);
                    FlightsListAdapter flightsAdapter = new FlightsListAdapter(ManageTripActivity.this, t.getFlights(), true, id);
                    flightList.setAdapter(flightsAdapter);
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

                // Update members list
                if (t.getMembers() != null) {
                    membersAdapter.setMembers(t.getMembers());
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ManageTripActivity.this, "Error loading trip: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void newActivityPopup(Trip trip, String id, Runnable onActivityAdded) {
        Dialog dialog = new Dialog(this, R.style.AlertDialogTheme);
        dialog.setContentView(R.layout.add_trip_activity_dialog);

        // Initialize views
        EditText activityName = dialog.findViewById(R.id.activity_name);
        EditText activityLocation = dialog.findViewById(R.id.activity_location);
        Button selectDate = dialog.findViewById(R.id.select_date);
        Button selectTime = dialog.findViewById(R.id.select_time);
        EditText costInput = dialog.findViewById(R.id.cost_input);
        AutoCompleteTextView currencySelector = dialog.findViewById(R.id.currency_selector);
        EditText noteInput = dialog.findViewById(R.id.note_input);
        Button submit = dialog.findViewById(R.id.submit_activity);

        // Setup currency selector
        CurrencyType[] currencies = {CurrencyType.USD, CurrencyType.EUR, CurrencyType.ILS};
        ArrayAdapter<CurrencyType> currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySelector.setAdapter(currencyAdapter);

        // Initialize date/time variables
        Calendar selectedDate = Calendar.getInstance();
        Calendar selectedTime = Calendar.getInstance();
        selectedTime.set(Calendar.HOUR_OF_DAY, 12);
        selectedTime.set(Calendar.MINUTE, 0);

        // Update button text with initial values
        updateDateButtonText(selectDate, selectedDate);
        updateTimeButtonText(selectTime, selectedTime);

        // Setup date picker
        selectDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    updateDateButtonText(selectDate, selectedDate);
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            );

            // Set min date to trip start date
            datePickerDialog.getDatePicker().setMinDate(trip.getStartDate().toDate().getTime());
            // Set max date to trip end date
            datePickerDialog.getDatePicker().setMaxDate(trip.getEndDate().toDate().getTime());

            datePickerDialog.show();
        });

        // Setup time picker
        selectTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedTime.set(Calendar.MINUTE, minute);
                    updateTimeButtonText(selectTime, selectedTime);
                },
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE),
                true
            );
            timePickerDialog.show();
        });


        // Setup submit button
        submit.setOnClickListener(v -> {
            String name = activityName.getText().toString();
            String location = activityLocation.getText().toString();
            String note = noteInput.getText().toString();
            String cost = costInput.getText().toString();
            String currency = currencySelector.toString();

            if (name.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Activity Name and Location are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate date/time selection
            if (selectedDate.getTimeInMillis() < trip.getStartDate().toDate().getTime() ||
                selectedDate.getTimeInMillis() > trip.getEndDate().toDate().getTime()) {
                Toast.makeText(this, "Date must be within trip dates", Toast.LENGTH_SHORT).show();
                return;
            }

            // Combine date and time
            Calendar finalDateTime = Calendar.getInstance();
            finalDateTime.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH),
                            selectedTime.get(Calendar.HOUR_OF_DAY), selectedTime.get(Calendar.MINUTE), 0);
            Timestamp timestamp = new Timestamp(finalDateTime.getTime());

            // Create activity
            ItineraryActivity activity = new ItineraryActivity(name, location, timestamp, note, cost);
            trip.addActivity(activity);
            onActivityAdded.run();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateDateButtonText(Button button, Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        button.setText(dateFormat.format(date.getTime()));
    }

    private void updateTimeButtonText(Button button, Calendar time) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        button.setText(timeFormat.format(time.getTime()));
    }
}