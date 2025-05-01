package com.idocalm.travelmate.models;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.enums.CurrencyType;

import java.util.ArrayList;

/**
 * The type User.
 */
public class User {
    private String name;
    private CurrencyType currency;
    private String id;
    private String imageUrl;
    private ArrayList<String> tripIds;
    private ArrayList<String> friendsIds;
    private String email;

    /**
     * Instantiates a new User.
     *
     * @param name         the name
     * @param currencyType the currency type
     * @param id           the id
     * @param trips        the trips
     * @param friends      the friends
     * @param imageUrl     the image url
     * @param email        the email
     */
    public User(String name, CurrencyType currencyType, String id, ArrayList<String> trips, ArrayList<String> friends, String imageUrl, String email) {
        this.name = name;
        this.currency = currencyType;
        this.id = id;
        this.tripIds = trips;
        this.friendsIds = friends;
        this.imageUrl = imageUrl;
        this.email = email;
    }

    /**
     * Gets friends ids.
     *
     * @return the friends ids
     */
    public ArrayList<String> getFriendsIds() {
        return friendsIds;
    }

    /**
     * Gets profile image.
     *
     * @return the profile image
     */
    public String getProfileImage() {
        return imageUrl;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets currency type.
     *
     * @return the currency type
     */
    public CurrencyType getCurrencyType() {
        return currency;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets trip ids.
     *
     * @return the trip ids
     */
    public ArrayList<String> getTripIds() {
        return tripIds;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {

        this.name = name;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(id).update("name", name);

    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets currency string.
     *
     * @return the currency string
     */
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

    /**
     * From document user.
     *
     * @param document the document
     * @return the user
     */
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

    /**
     * Sets currency type.
     *
     * @param currencyType the currency type
     */
    public void setCurrencyType(CurrencyType currencyType) {
        this.currency = currencyType;
    }

    /**
     * Add trip id.
     *
     * @param tripId the trip id
     */
    public void addTripId(String tripId) {
        tripIds.add(tripId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(id).update("tripIds", tripIds);

    }

    /**
     * Sets friends ids.
     *
     * @param friends the friends
     */
    public void setFriendsIds(ArrayList<String> friends) {
        this.friendsIds = friends;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(id).update("friendsIds", friends);
    }

    /**
     * Sets profile.
     *
     * @param imageUrl the image url
     */
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

    /**
     * Add friend.
     *
     * @param friendId the friend id
     * @param context  the context
     */
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
