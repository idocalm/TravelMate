package com.idocalm.travelmate.models;

/**
 * The type Breadcrumb event.
 */
public class BreadcrumbEvent {
    private String title;
    private Runnable onClick;

    /**
     * Instantiates a new Breadcrumb event.
     *
     * @param title   the title
     * @param onClick the on click
     */
    public BreadcrumbEvent(String title, Runnable onClick) {
        this.title = title;
        this.onClick = onClick;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets on click.
     *
     * @return the on click
     */
    public Runnable getOnClick() {
        return onClick;
    }
}
