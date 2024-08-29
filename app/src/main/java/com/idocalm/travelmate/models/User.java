package com.idocalm.travelmate.models;

import com.idocalm.travelmate.enums.CurrencyType;

public class User {
    private String name;
    private CurrencyType currency;
    private String id;

    public User(String name, CurrencyType currencyType, String id) {
        this.name = name;
        this.currency = currencyType;
        this.id = id;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setCurrencyType(CurrencyType currencyType) {
        this.currency = currencyType;
    }

}
