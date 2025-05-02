package com.idocalm.travelmate.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.idocalm.travelmate.R;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.models.ItineraryActivity;

import java.util.HashMap;
import java.util.List;

public class ActivitiesExpandableAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> dateList;
    private final HashMap<String, List<ItineraryActivity>> activitiesMap;

    public ActivitiesExpandableAdapter(Context context, List<String> dateList, HashMap<String, List<ItineraryActivity>> activitiesMap) {
        this.context = context;
        this.dateList = dateList;
        this.activitiesMap = activitiesMap;
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

        return convertView;
    }

    @Override public boolean isChildSelectable(int i, int i1) { return false; }
}
