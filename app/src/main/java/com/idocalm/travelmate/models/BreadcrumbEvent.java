package com.idocalm.travelmate.models;

public class BreadcrumbEvent {
    private String title;
    private Runnable onClick;

    public BreadcrumbEvent(String title, Runnable onClick) {
        this.title = title;
        this.onClick = onClick;
    }

    public String getTitle() {
        return title;
    }

    public Runnable getOnClick() {
        return onClick;
    }
}
