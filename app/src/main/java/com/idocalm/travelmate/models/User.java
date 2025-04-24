package com.idocalm.travelmate.models;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.enums.CurrencyType;

import java.util.ArrayList;

public class User {
    private String name;
    private CurrencyType currency;
    private String id;
    private String imageUrl;
    private ArrayList<String> tripIds;
    private ArrayList<String> friendsIds;
    private String email;

    public User(String name, CurrencyType currencyType, String id, ArrayList<String> trips, ArrayList<String> friends, String imageUrl, String email) {
        this.name = name;
        this.currency = currencyType;
        this.id = id;
        this.tripIds = trips;
        this.friendsIds = friends;
        this.imageUrl = imageUrl;
        this.email = email;
    }

    public ArrayList<String> getFriendsIds() {
        return friendsIds;
    }

    public String getProfileImage() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public CurrencyType getCurrencyType() {
        return currency;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getTripIds() {
        return tripIds;
    }

    public void setName(String name) {

        this.name = name;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(id).update("name", name);

    }

    public String getEmail() {
        return email;
    }

    public String getCurrencyString() {
        if (currency == CurrencyType.NONE) {
            return "None";
        } else if (currency == CurrencyType.USD) {
            return "USD";
        } else if (currency == CurrencyType.EUR) {
            return "EUR";
        } else {
            return "ILS";
        }
    }

    public static User fromDocument(DocumentSnapshot document) {
        String name = document.getString("name");
        String id = document.getString("id");
        String imageUrl = document.getString("profile");
        ArrayList<String> trips = (ArrayList<String>) document.get("tripIds");
        if (trips == null) {
            trips = new ArrayList<>();
        }
        ArrayList<String> friends = (ArrayList<String>) document.get("friendsIds");
        if (friends == null) {
            friends = new ArrayList<>();
        }

        CurrencyType currencyType;
        switch (document.getString("currency")) {
            case "USD":
                currencyType = CurrencyType.USD;
                break;
            case "EUR":
                currencyType = CurrencyType.EUR;
                break;
            case "ILS":
                currencyType = CurrencyType.ILS;
                break;
            default:
                currencyType = CurrencyType.NONE;
                break;
        }

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        return new User(name, currencyType, id, trips, friends, imageUrl, email);
    }

    public void setCurrencyType(CurrencyType currencyType) {
        this.currency = currencyType;
    }

    public void addTripId(String tripId) {
        tripIds.add(tripId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(id).update("tripIds", tripIds);

    }

    public void setFriendsIds(ArrayList<String> friends) {
        this.friendsIds = friends;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(id).update("friendsIds", friends);
    }

    public void setProfile(String imageUrl) {
        this.imageUrl = imageUrl;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(id).update("profile", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(null, "Profile image updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(null, "Failed to update profile image", Toast.LENGTH_SHORT).show();
                });
    }

    public void addFriend(String friendId, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(friendId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User friend = User.fromDocument(documentSnapshot);
                this.friendsIds.add(friend.getId());
                friend.friendsIds.add(this.id);

                db.collection("users").document(this.id).update("friendsIds", this.friendsIds);
                db.collection("users").document(friend.getId()).update("friendsIds", friend.friendsIds);

                Toast.makeText(context, "Friend added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Failed to add friend", Toast.LENGTH_SHORT).show();
        });
    }
}
