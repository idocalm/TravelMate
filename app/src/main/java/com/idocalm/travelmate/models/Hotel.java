package com.idocalm.travelmate.models;

/**
 * The type Hotel.
 */
public class Hotel {
    private int id;
    private String name;
    private String mainPhoto;
    private double latitude;
    private double longitude;
    private double price;
    private String checkOutDate;
    private String checkInDate;


    /**
     * Instantiates a new Hotel.
     *
     * @param id           the id
     * @param name         the name
     * @param mainPhoto    the main photo
     * @param latitude     the latitude
     * @param longitude    the longitude
     * @param price        the price
     * @param checkInDate  the check in date
     * @param checkOutDate the check out date
     */
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

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets main photo.
     *
     * @return the main photo
     */
    public String getMainPhoto() {
        return mainPhoto;
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
     * Gets latitude.
     *
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets longitude.
     *
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Gets check in date.
     *
     * @return the check in date
     */
    public String getCheckInDate() {
        return checkInDate;
    }

    /**
     * Gets check out date.
     *
     * @return the check out date
     */
    public String getCheckOutDate() {
        return checkOutDate;
    }


}
