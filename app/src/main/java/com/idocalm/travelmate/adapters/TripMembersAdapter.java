package com.idocalm.travelmate.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.models.Trip;
import com.idocalm.travelmate.models.User;

import java.util.ArrayList;
import java.util.List;

public class TripMembersAdapter extends RecyclerView.Adapter<TripMembersAdapter.MemberViewHolder> {
    private List<String> memberIds;
    private Context context;
    private boolean isOwner;
    private String tripId;

    public TripMembersAdapter(Context context, String tripId, boolean isOwner) {
        this.context = context;
        this.memberIds = new ArrayList<>();
        this.isOwner = isOwner;
        this.tripId = tripId;
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


        holder.profileImage.setOnClickListener(v -> {
            Log.d("TripMembersAdapter", "Member clicked: " + memberId);
            Log.d("TripMembersAdapter", "Is owner: " + isOwner);
            Log.d("TripMembersAdapter", "User ID: " + Auth.getUser().getId());

            if (isOwner && !memberId.equals(Auth.getUser().getId())) {
                // show dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Remove Member");
                builder.setMessage("Are you sure you want to remove this member from the trip?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    // remove member from the trip
                    com.google.firebase.firestore.FirebaseFirestore.getInstance()
                            .collection("trips")
                            .document(tripId)
                            .update("members", com.google.firebase.firestore.FieldValue.arrayRemove(memberId))
                            .addOnSuccessListener(aVoid -> {
                                // handle success
                                Auth.getUser().getTrip(tripId, new Trip.TripCallback() {
                                    @Override
                                    public void onTripLoaded(Trip trip) {
                                        trip.removeMember(memberId);

                                        FirebaseFirestore.getInstance()
                                                .collection("users")
                                                .document(memberId).get().addOnSuccessListener(document -> {
                                                    if (document.exists()) {
                                                        ArrayList<String> trips = (ArrayList<String>) document.get("tripIds");
                                                        if (trips != null) {
                                                            trips.remove(tripId);
                                                            FirebaseFirestore.getInstance()
                                                                    .collection("users")
                                                                    .document(memberId)
                                                                    .update("tripIds", trips);
                                                        }
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });

                                memberIds.remove(memberId);
                                notifyItemRemoved(position);
                            })
                            .addOnFailureListener(e -> {
                                // handle failure
                            });
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();
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