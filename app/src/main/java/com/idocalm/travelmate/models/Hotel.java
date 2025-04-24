package com.idocalm.travelmate.models;

public class Hotel {
    private int id;
    private String name;
    private String mainPhoto;
    private double latitude;
    private double longitude;
    private double price;
    private String checkOutDate;
    private String checkInDate;


    public Hotel(int id, String name, String mainPhoto, double latitude, double longitude, long price, String checkInDate, String checkOutDate) {
        this.id = id;
        this.name = name;
        this.mainPhoto = mainPhoto;
        this.latitude = latitude;
        this.longitude = longitude;
        this.price = price;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;

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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }


}
