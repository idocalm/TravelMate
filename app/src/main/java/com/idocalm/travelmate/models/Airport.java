package com.idocalm.travelmate.models;

/**
 * The type Airport.
 */
public class Airport {
    private String iata;
    private String city;
    private String name;

    /**
     * Instantiates a new Airport.
     *
     * @param iata the iata
     * @param city the city
     * @param name the name
     */
    public Airport(String iata, String city, String name) {
        this.iata = iata;
        this.city = city;
        this.name = name;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return iata;
    }

    /**
     * Gets city.
     *
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

}
