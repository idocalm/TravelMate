package com.idocalm.travelmate.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Hotel {
    private int id;
    private String name;
    private String mainPhoto;
    private double latitude;
    private double longitude;
    private double price;

    public Hotel(int id, String name, String mainPhoto, double latitude, double longitude, long price) {
        this.id = id;
        this.name = name;
        this.mainPhoto = mainPhoto;
        this.latitude = latitude;
        this.longitude = longitude;
        this.price = price;
    }

    public String getMainPhoto() {
        return mainPhoto;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return (int) price;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }


}
