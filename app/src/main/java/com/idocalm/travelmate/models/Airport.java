package com.idocalm.travelmate.models;

public class Airport {
    private String iata;
    private String city;
    private String name;

    public Airport(String iata, String city, String name) {
        this.iata = iata;
        this.city = city;
        this.name = name;
    }

    public String getId() {
        return iata;
    }

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

}
