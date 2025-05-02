package com.idocalm.travelmate.models;

import java.util.Date;
import java.util.HashMap;

/**
 * The type Hotel.
 */
public class Hotel {
    private int id;
    private String name;
    private String mainPhoto;
    private double price;
    private Date checkOutDate;
    private Date checkInDate;


    /**
     * Instantiates a new Hotel.
     *
     * @param id           the id
     * @param name         the name
     * @param mainPhoto    the main photo

     * @param price        the price
     * @param checkInDate  the check in date
     * @param checkOutDate the check out date
     */
    public Hotel(int id, String name, String mainPhoto, long price, Date checkInDate, Date checkOutDate) {
        this.id = id;
        this.name = name;
        this.mainPhoto = mainPhoto;
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
     * Gets price.
     *
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Gets check in date.
     *
     * @return the check in date
     */
    public Date getCheckInDate() {
        return checkInDate;
    }

    /**
     * Gets check out date.
     *
     * @return the check out date
     */
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

}
