package com.idocalm.travelmate.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.models.User;

import java.util.ArrayList;
import java.util.List;

public class TripMembersAdapter extends RecyclerView.Adapter<TripMembersAdapter.MemberViewHolder> {
    private List<String> memberIds;
    private Context context;

    public TripMembersAdapter(Context context) {
        this.context = context;
        this.memberIds = new ArrayList<>();
    }

    public void setMembers(List<String> memberIds) {
        this.memberIds = memberIds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trip_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        String memberId = memberIds.get(position);
        
        // Load user data from Firestore
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
            .collection("users")
            .document(memberId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    User user = User.fromDocument(documentSnapshot);
                    if (user != null && user.getProfileImage() != null) {
                        Glide.with(context)
                            .load(user.getProfileImage())
                            .placeholder(R.drawable.profile_placeholder)
                            .error(R.drawable.profile_placeholder)
                            .circleCrop()
                            .into(holder.profileImage);
                    }
                }
            });
    }

    @Override
    public int getItemCount() {
        return memberIds.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;

        MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.member_profile_image);
        }
    }
} 