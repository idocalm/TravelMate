package com.idocalm.travelmate.components.friends;


import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.models.User;

import java.util.ArrayList;

public class FriendsListAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<User> friendsListData;

    public FriendsListAdapter(Context context, ArrayList<User> friendsListData) {
        this.context = context;
        this.friendsListData = friendsListData;

    }

    @Override
    public int getCount() {
        // Return the number of items in the list
        return friendsListData.size();
    }


    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View view = inflater.inflate(R.layout.fragment_friend_card, container, false);

        view.setOnClickListener(v -> {
            Toast.makeText(context, "Long press to remove friend", Toast.LENGTH_SHORT).show();
        });

        view.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Remove Friend");
            builder.setMessage("Are you sure you want to remove this friend?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                // remove friend from the list
                this.friendsListData.remove(position);
                ArrayList<String> friendsIds = new ArrayList<>();
                for (User friend : this.friendsListData) {
                    friendsIds.add(friend.getId());
                }

                Auth.getUser().setFriendsIds(friendsIds);
                Toast.makeText(context, "Friend removed", Toast.LENGTH_SHORT).show();

                // Notify the adapter about the data change
                notifyDataSetChanged();
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
            builder.show();
            return true;
        });


        User friend = friendsListData.get(position);

        TextView friendName = view.findViewById(R.id.profile_name);
        TextView friendEmail = view.findViewById(R.id.profile_email);
        ImageView friendImage = view.findViewById(R.id.friend_profile_img);


        friendName.setText(friend.getName());
        friendEmail.setText(friend.getEmail());
        Glide.with(context).load(friend.getProfileImage()).into(friendImage);

        container.addView(view);

        return view;

    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE; // Force ViewPager to refresh everything
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

}