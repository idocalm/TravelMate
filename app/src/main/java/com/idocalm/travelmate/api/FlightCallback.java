package com.idocalm.travelmate.api;

import com.idocalm.travelmate.models.Flight;

import java.util.ArrayList;

public interface FlightCallback {
    void onFlightsFetched(ArrayList<Flight> flights);
    void onError(Exception e);
}
