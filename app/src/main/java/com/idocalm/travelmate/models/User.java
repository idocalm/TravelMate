package com.idocalm.travelmate.models;

import com.idocalm.travelmate.enums.CurrencyType;

import java.util.ArrayList;

public class User {
    private String name;
    private CurrencyType currency;
    private String id;
    private ArrayList<String> tripIds;

    public User(String name, CurrencyType currencyType, String id, ArrayList<String> trips) {
        this.name = name;
        this.currency = currencyType;
        this.id = id;
        this.tripIds = trips;
    }

    public User(String name, CurrencyType currency) {
        this.name = name;
        this.currency = currency;
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
    }

    public void setCurrencyType(CurrencyType currencyType) {
        this.currency = currencyType;
    }


}
