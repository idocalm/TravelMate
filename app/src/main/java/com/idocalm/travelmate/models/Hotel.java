package com.idocalm.travelmate.models;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class Hotel {
    private int id;
    private String dbId;
    private String name;
    private String mainPhoto;
    private double price;
    private Date checkOutDate;
    private Date checkInDate;
    private String currency;


    public Hotel(int id, String name, String mainPhoto, long price, Date checkInDate, Date checkOutDate, String currency) {
        this.id = id;
        this.dbId = "";
        this.name = name;
        this.mainPhoto = mainPhoto;
        this.price = price;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.currency = currency;
    }

    public Hotel(String dbId, String name, String mainPhoto, double price, Date checkInDate, Date checkOutDate, String currency) {
        this.dbId = dbId;
        this.name = name;
        this.mainPhoto = mainPhoto;
        this.price = price;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.currency = currency;
    }


    public interface HotelsCallback {
        void onHotelsLoaded(ArrayList<Hotel> hotels);
        void onError(Exception e);
    }
    public int getId() {
        return id;
    }

    public String getMainPhoto() {
        return mainPhoto;
    }

    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDBId() {
        return dbId;
    }
    public double getPrice() {
        return price;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }


    public static HashMap<String, Object> toHashMap(Hotel hotel) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("id", String.valueOf(hotel.getId()));
        data.put("name", hotel.getName());
        data.put("mainPhoto", hotel.getMainPhoto());
        data.put("price", String.valueOf(hotel.getPrice()));
        data.put("checkInDate", hotel.getCheckInDate());
        data.put("checkOutDate", hotel.getCheckOutDate());

        return data;
    }

    public static void deleteHotel(String tripId, String hotelId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("trips").document(tripId).collection("hotels").document(hotelId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Hotel", "Hotel successfully deleted!");
                })
                .addOnFailureListener(e -> {
                    Log.w("Hotel", "Error deleting hotel", e);
                });
    }

}
