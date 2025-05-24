package com.idocalm.travelmate.components;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.models.ItineraryActivity;
import com.idocalm.travelmate.models.Trip;
import com.idocalm.travelmate.enums.CurrencyType;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import java.util.Calendar;
import android.widget.Spinner;

public class ActivitiesExpandableAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> dateList;
    private final Trip trip;
    private final HashMap<String, List<ItineraryActivity>> activitiesMap;
    private final Activity activity;

    public ActivitiesExpandableAdapter(Context context, Activity activity, Trip trip, List<String> dateList, HashMap<String, List<ItineraryActivity>> activitiesMap) {
        this.context = context;
        this.dateList = dateList;
        this.activitiesMap = activitiesMap;
        this.trip = trip;
        this.activity = activity;
    }

    @Override public int getGroupCount() { return dateList.size(); }
    @Override public int getChildrenCount(int groupPosition) { return activitiesMap.get(dateList.get(groupPosition)).size(); }
    @Override public Object getGroup(int groupPosition) { return dateList.get(groupPosition); }
    @Override public Object getChild(int groupPosition, int childPosition) {
        return activitiesMap.get(dateList.get(groupPosition)).get(childPosition);
    }
    @Override public long getGroupId(int groupPosition) { return groupPosition; }
    @Override public long getChildId(int groupPosition, int childPosition) { return childPosition; }
    @Override public boolean hasStableIds() { return false; }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String date = dateList.get(groupPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.expandable_list_group, parent, false);
        }

        ImageView indicator = convertView.findViewById(R.id.group_indicator);
        if (isExpanded) {
            indicator.setImageResource(R.drawable.down_arrow);
        } else {
            indicator.setImageResource(R.drawable.right_arrow);
        }

        ((TextView) convertView.findViewById(R.id.group_date)).setText(date);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {

        ItineraryActivity activity = (ItineraryActivity) getChild(groupPosition, childPosition);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.trip_activity_card, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.activity_card_name)).setText(activity.getName());
        ((TextView) convertView.findViewById(R.id.activity_card_location)).setText(activity.getLocation());

        if (activity.getCost() != null && !activity.getCost().isEmpty()) {
            String currency = Auth.getUser().getCurrencyString();
            ((TextView) convertView.findViewById(R.id.activity_card_price)).setText(currency + " " + activity.getCost());
        }

        Button delete = convertView.findViewById(R.id.delete_activity);
        Button edit =  convertView.findViewById(R.id.edit_activity);

        edit.setOnClickListener(v -> {
            editActivityPopup(activity, () -> {
                this.activity.recreate();
            });
        });

        delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Activity")
                    .setMessage("Are you sure you want to delete this activity?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        trip.removeActivity(activity);

                        activitiesMap.get(dateList.get(groupPosition)).remove(activity);

                        if (activitiesMap.get(dateList.get(groupPosition)).isEmpty()) {
                            activitiesMap.remove(dateList.get(groupPosition));
                            dateList.remove(groupPosition);
                        }

                        if (activitiesMap.isEmpty()) {
                            ((LinearLayout) ((Activity) context).findViewById(R.id.no_activities)).setVisibility(View.VISIBLE);
                        } else {
                            ((LinearLayout) ((Activity) context).findViewById(R.id.no_activities)).setVisibility(View.GONE);
                        }
                        notifyDataSetChanged();

                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();

        });



        return convertView;
    }

    public void editActivityPopup(ItineraryActivity activity, Runnable onActivityEdited) {
        Dialog dialog = new Dialog(context, R.style.AlertDialogTheme);
        dialog.setContentView(R.layout.add_trip_activity_dialog);
        // Initialize views
        EditText nameEdit = dialog.findViewById(R.id.activity_name);
        Button selectDate = dialog.findViewById(R.id.select_date);
        Button selectTime = dialog.findViewById(R.id.select_time);
        EditText locationEdit = dialog.findViewById(R.id.activity_location);
        EditText noteInput = dialog.findViewById(R.id.note_input);
        EditText costInput = dialog.findViewById(R.id.cost_input);
        AutoCompleteTextView currencySelector = dialog.findViewById(R.id.currency_selector);
        Button submit = dialog.findViewById(R.id.submit_activity);

        // Setup currency selector
        CurrencyType[] currencies = {CurrencyType.USD, CurrencyType.EUR, CurrencyType.ILS};
        ArrayAdapter<CurrencyType> currencyAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, currencies);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySelector.setAdapter(currencyAdapter);

        // Initialize date/time variables
        Calendar selectedDate = Calendar.getInstance();
        Calendar selectedTime = Calendar.getInstance();
        selectedDate.setTime(activity.getDate().toDate());
        selectedTime.setTime(activity.getDate().toDate());

        // Update button text with initial values
        updateDateButtonText(selectDate, selectedDate);
        updateTimeButtonText(selectTime, selectedTime);

        // Set initial values
        nameEdit.setText(activity.getName());
        locationEdit.setText(activity.getLocation());

        // Setup date picker
        selectDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
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
                context,
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
            String name = nameEdit.getText().toString();
            String location = locationEdit.getText().toString();
            String note = noteInput.getText().toString();
            String cost = costInput.getText().toString();
            String currency = currencySelector.toString();

            if (name.isEmpty() || location.isEmpty()) {
                Toast.makeText(context, "Activity Name and Location are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate date/time selection
            if (selectedDate.getTimeInMillis() < trip.getStartDate().toDate().getTime() ||
                selectedDate.getTimeInMillis() > trip.getEndDate().toDate().getTime()) {
                Toast.makeText(context, "Date must be within trip dates", Toast.LENGTH_SHORT).show();
                return;
            }

            // Combine date and time
            Calendar finalDateTime = Calendar.getInstance();
            finalDateTime.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH),
                            selectedTime.get(Calendar.HOUR_OF_DAY), selectedTime.get(Calendar.MINUTE), 0);
            Timestamp timestamp = new Timestamp(finalDateTime.getTime());

            // Create activity
            ItineraryActivity newActivity = new ItineraryActivity(name, location, timestamp, note, cost);
            this.trip.editActivity(activity, newActivity);
            onActivityEdited.run();
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

    @Override public boolean isChildSelectable(int i, int i1) { return false; }
}
