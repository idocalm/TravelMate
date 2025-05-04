package com.idocalm.travelmate.components;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

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
        Dialog dialog = new Dialog(context);

        // change dialog width
        AtomicReference<Boolean> noteOpen = new AtomicReference<>(false);
        AtomicReference<Boolean> costOpen = new AtomicReference<>(false);

        dialog.setContentView(R.layout.add_trip_activity_dialog);

        Button toggleNote = dialog.findViewById(R.id.add_activity_note);
        Button toggleCost = dialog.findViewById(R.id.add_activity_cost);

        EditText nameEdit = dialog.findViewById(R.id.activity_name);
        EditText dateEdit = dialog.findViewById(R.id.activity_date);
        EditText timeEdit = dialog.findViewById(R.id.activity_time);
        EditText locationEdit = dialog.findViewById(R.id.activity_location);

        dateEdit.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(activity.getDate().toDate()));
        timeEdit.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(activity.getDate().toDate()));

        nameEdit.setText(activity.getName());
        locationEdit.setText(activity.getLocation());


        if (activity.getNote() != null && !activity.getNote().isEmpty()) {
            ((EditText) dialog.findViewById(R.id.activity_note)).setText(activity.getNote());
            dialog.findViewById(R.id.activity_note).setVisibility(LinearLayout.VISIBLE);
            noteOpen.set(true);
            toggleNote.setText("Del. Note");
        } else {
            dialog.findViewById(R.id.activity_note).setVisibility(LinearLayout.GONE);
            toggleNote.setText("Add Note");
        }

        if (activity.getCost() != null && !activity.getCost().isEmpty()) {
            ((EditText) dialog.findViewById(R.id.activity_cost)).setText(activity.getCost());
            dialog.findViewById(R.id.activity_cost).setVisibility(LinearLayout.VISIBLE);
            costOpen.set(true);
            toggleCost.setText("Del. Cost");
        } else {
            dialog.findViewById(R.id.activity_cost).setVisibility(LinearLayout.GONE);
            toggleCost.setText("Add Cost");
        }

        ((EditText) dialog.findViewById(R.id.activity_name)).setText(activity.getName());
        ((EditText) dialog.findViewById(R.id.activity_location)).setText(activity.getLocation());

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
            String activityName = nameEdit.getText().toString();
            String location = locationEdit.getText().toString();
            String note = ((EditText) dialog.findViewById(R.id.activity_note)).getText().toString();
            String cost = ((EditText) dialog.findViewById(R.id.activity_cost)).getText().toString();

            if (activityName.isEmpty() || location.isEmpty()) {
                Toast.makeText(context, "Activity Name and Location are required", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, "Date is required", Toast.LENGTH_SHORT).show();
                return;
            }

            DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date;
            try {
                date = df.parse(dateField.getText().toString());
            } catch (Exception e) {
                Toast.makeText(context, "Invalid date format (d/m/y)", Toast.LENGTH_SHORT).show();
                return;
            }

            // combine date and time to a timestamp
            String timeStr = ((EditText) dialog.findViewById(R.id.activity_time)).getText().toString();
            Time time;
            try {
                time = Time.valueOf(timeStr + ":00");
            } catch (Exception e) {
                Toast.makeText(context, "Invalid time format (hour:minute)", Toast.LENGTH_SHORT).show();
                return;
            }

            date.setHours(time.getHours());
            date.setMinutes(time.getMinutes());
            date.setSeconds(0);
            Timestamp timestamp = new Timestamp(date);

            ItineraryActivity newActivity = new ItineraryActivity(activityName, location, timestamp, note, cost);
            this.trip.editActivity(activity, newActivity);
            onActivityEdited.run();

            dialog.dismiss();
        });

        dialog.show();
    }

    @Override public boolean isChildSelectable(int i, int i1) { return false; }
}
