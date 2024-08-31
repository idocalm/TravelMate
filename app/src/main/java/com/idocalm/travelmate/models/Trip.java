package com.idocalm.travelmate.models;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Trip {
    private String name;
    private String location;
    private String owner;
    private String description;
    private Timestamp start_date;
    private Timestamp end_date;
    private Timestamp created_at;
    private Timestamp last_edited;
    private Timestamp last_opened;
    private String image;

    public Trip(String name, String location, String owner, String description, String image, Timestamp start_date, Timestamp end_date, Timestamp created_at, Timestamp last_edited, Timestamp last_opened) {
        this.name = name;
        this.location = location;
        this.owner = owner;
        this.description = description;
        this.image = image;
        this.start_date = start_date;
        this.end_date = end_date;
        this.created_at = created_at;
        this.last_edited = last_edited;
        this.last_opened = last_opened;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getOwner() {
        return owner;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }
    public Timestamp getStartDate() {
        return start_date;
    }

    public Timestamp getEndDate() {
        return end_date;
    }

    public Timestamp getCreatedAt() {
        return created_at;
    }

    public Timestamp getLastEdited() {
        return last_edited;
    }

    public Timestamp getLastOpened() {
        return last_opened;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDate(Timestamp start_date) {
        this.start_date = start_date;
    }

    public void setEndDate(Timestamp end_date) {
        this.end_date = end_date;
    }

    public void setCreatedAt(Timestamp created_at) {
        this.created_at = created_at;
    }

    public void setLastEdited(Timestamp last_edited) {
        this.last_edited = last_edited;
    }

    public void setLastOpened(Timestamp last_opened) {
        this.last_opened = last_opened;
    }

    public void save() {
        // TODO: save the trip to the database
    }




}
