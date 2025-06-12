package com.idocalm.travelmate.components.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.api.CTranslator;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.models.Flight;
import com.idocalm.travelmate.models.Hotel;
import com.idocalm.travelmate.models.ItineraryActivity;
import com.idocalm.travelmate.models.Trip;
import com.idocalm.travelmate.models.User;

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class TotalBalanceFragment extends Fragment {

    public TotalBalanceFragment() {}

    public interface Callback {
        void onSuccess(double total);
        void onFailure(Exception e);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static void loadTripExpenses(Trip trip, TotalBalanceFragment.Callback callback) {
        ArrayList<Hotel> hotels = trip.getHotels();
        ArrayList<Integer> handledHotels = new ArrayList<>();
        Map<String, Double> totals = new HashMap<>();
        totals.put("hotels", 0.0);
        totals.put("activities", 0.0);
        totals.put("flights", 0.0);

        for (Hotel hotel : hotels) {
            CTranslator.translate((int) hotel.getPrice(), hotel.getCurrency(), Auth.getUser().getCurrencyString(), new CTranslator.TranslationCallback() {
                @Override
                public void onSuccess(double result) {
                    Log.d("TotalBalanceFragment", "Hotel price translated: " + result);
                    totals.put("hotels", totals.get("hotels") + result);

                    handledHotels.add(hotel.getId());
                    if (handledHotels.size() == hotels.size()) {
                        loadTripActivities(trip, totals, callback);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("TotalBalanceFragment", "Error translating hotel price: " + e.getMessage());
                    e.printStackTrace();
                }

            });

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_total_balance, container, false);

        User user = Auth.getUser();
        ArrayList<String> ids = user.getTripIds();

        // Get the total balance of all trips
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<String> handled = new ArrayList<>();

        if (ids.size() > 0) {
            // Get the trip balance
            Map<String, Double> totals = new HashMap<>();
            totals.put("hotels", 0.0);
            totals.put("activities", 0.0);
            totals.put("flights", 0.0);

            for (String id : ids) {
                db.collection("trips").document(id).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Trip.fromDB(documentSnapshot, new Trip.TripCallback() {
                            @Override
                            public void onTripLoaded(Trip trip) {
                                Log.d("TotalBalanceFragment", "Trip loaded: " + trip.getName());
                                ArrayList<Hotel> hotels = trip.getHotels();
                                ArrayList<Integer> handledHotels = new ArrayList<>();

                                if (hotels.size() == 0) {
                                    Log.d("TotalBalanceFragment", "No hotels found for trip: " + trip.getName());
                                    loadTripActivities(trip, totals, ids, handled, view);
                                    return;
                                }

                                for (Hotel hotel : hotels) {
                                    CTranslator.translate((int) hotel.getPrice(), hotel.getCurrency(), Auth.getUser().getCurrencyString(), new CTranslator.TranslationCallback() {
                                        @Override
                                        public void onSuccess(double result) {
                                            Log.d("TotalBalanceFragment", "Hotel price translated: " + result);
                                            totals.put("hotels", totals.get("hotels") + result);

                                            handledHotels.add(hotel.getId());
                                            if (handledHotels.size() == hotels.size()) {
                                                loadTripActivities(trip, totals, ids, handled, view);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            Log.e("TotalBalanceFragment", "Error translating hotel price: " + e.getMessage());
                                            e.printStackTrace();
                                        }

                                    });

                                }

                            }

                            @Override
                            public void onError(Exception e) {
                                e.printStackTrace();
                                Log.d("TotalBalanceFragment", "Error loading trip: " + e.getMessage());
                            }
                        });
                    }
                });
            }
        }

        return view;

    }

    private static void loadTripActivities(Trip trip, Map<String, Double> totals, TotalBalanceFragment.Callback callback) {
        ArrayList<ItineraryActivity> activities = trip.getActivities();
        ArrayList<Integer> handledActivities = new ArrayList<>();

        if (activities.size() == 0) {
            Log.d("TotalBalanceFragment", "No activities found for trip: " + trip.getName());
            loadTripFlights(trip, totals, callback);
        }

        for (ItineraryActivity activity : activities) {
            CTranslator.translate((int) activity.getCost(), activity.getCurrency(), Auth.getUser().getCurrencyString(), new CTranslator.TranslationCallback() {
                @Override
                public void onSuccess(double result) {
                    totals.put("activities", totals.get("activities") + result);
                    Log.d("TotalBalanceFragment", "Activity cost translated: " + result);
                    handledActivities.add(activity.getDate().hashCode());

                    if (handledActivities.size() == activities.size()) {
                        loadTripFlights(trip, totals, callback);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("TotalBalanceFragment", "Error translating activity cost: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            Log.d("TotalBalanceFragment", "Adding activity cost: " + activity.getCost());
        }
    }

    private void loadTripActivities(Trip trip, Map<String, Double> totals, ArrayList<String> ids, ArrayList<String> handled, View view) {
        ArrayList<ItineraryActivity> activities = trip.getActivities();
        ArrayList<Integer> handledActivities = new ArrayList<>();

        if (activities.size() == 0) {
            Log.d("TotalBalanceFragment", "No activities found for trip: " + trip.getName());
            loadTripFlights(trip, totals, ids, handled, view);
            return;
        }

        for (ItineraryActivity activity : activities) {
            CTranslator.translate((int) activity.getCost(), activity.getCurrency(), Auth.getUser().getCurrencyString(), new CTranslator.TranslationCallback() {
                @Override
                public void onSuccess(double result) {
                    totals.put("activities", totals.get("activities") + result);
                    Log.d("TotalBalanceFragment", "Activity cost translated: " + result);
                    handledActivities.add(activity.getDate().hashCode());

                    if (handledActivities.size() == activities.size()) {
                        loadTripFlights(trip, totals, ids, handled, view);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("TotalBalanceFragment", "Error translating activity cost: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            Log.d("TotalBalanceFragment", "Adding activity cost: " + activity.getCost());
        }


    }

    private static void loadTripFlights(Trip trip, Map<String, Double> totals, TotalBalanceFragment.Callback callback) {
        ArrayList<Flight> flights = trip.getFlights();
        ArrayList<Integer> handledFlights = new ArrayList<>();

        if (flights.size() == 0) {
            Log.d("TotalBalanceFragment", "No flights found for trip: " + trip.getName());
            callback.onSuccess(totals.get("activities") + totals.get("hotels") + totals.get("flights"));
            return;
        }

        for (Flight flight : flights) {
            CTranslator.translate((int) flight.price, flight.currency, Auth.getUser().getCurrencyString(), new CTranslator.TranslationCallback() {
                @Override
                public void onSuccess(double result) {
                    totals.put("flights", totals.get("flights") + result);
                    Log.d("TotalBalanceFragment", "Flight cost translated: " + result);
                    handledFlights.add(flight.hashCode());

                    if (handledFlights.size() == flights.size()) {
                        // run the callback with the totals
                        Log.d("TotalBalanceFragment", "All flight costs translated, returning total balance");
                        double total = totals.get("activities") + totals.get("hotels") + totals.get("flights");
                        callback.onSuccess(total);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("TotalBalanceFragment", "Error translating flight cost: " + e.getMessage());
                    e.printStackTrace();
                    callback.onFailure(e);
                }
            });
        }
    }

    private void loadTripFlights(Trip trip, Map<String, Double> totals, ArrayList<String> ids, ArrayList<String> handled, View view) {
        ArrayList<Flight> flights = trip.getFlights();
        ArrayList<Integer> handledFlights = new ArrayList<>();

        if (flights.size() == 0) {
            Log.d("TotalBalanceFragment", "No flights found for trip: " + trip.getName());
            if (getActivity() != null)
                requireActivity().runOnUiThread(() -> updateUI(totals, view));
            return;
        }

        for (Flight flight : flights) {
            CTranslator.translate((int) flight.price, flight.currency, Auth.getUser().getCurrencyString(), new CTranslator.TranslationCallback() {
                @Override
                public void onSuccess(double result) {
                    totals.put("flights", totals.get("flights") + result);
                    Log.d("TotalBalanceFragment", "Flight cost translated: " + result);
                    handledFlights.add(flight.hashCode());

                    if (handledFlights.size() == flights.size()) {
                        Log.d("TotalBalanceFragment", "All flight costs translated, updating UI");
                        if (getActivity() != null)
                            requireActivity().runOnUiThread(() -> updateUI(totals, view));
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("TotalBalanceFragment", "Error translating flight cost: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }
    }

    private void updateUI(Map<String, Double> totals, View view) {
        TextView totalActivities = view.findViewById(R.id.balance_activities);
        TextView totalHotels = view.findViewById(R.id.balance_hotels);
        TextView totalFlights = view.findViewById(R.id.balance_flights);

        // get the current values from the text views and + the totals
        if (totals.get("activities") == null) totals.put("activities", 0.0);
        if (totals.get("hotels") == null) totals.put("hotels", 0.0);
        if (totals.get("flights") == null) totals.put("flights", 0.0);

        Log.d("TotalBalanceFragment", "Updating UI with totals: " + totals);

        Log.d("TOTAL", "total hotels: " + totalHotels.getText().toString());
        Log.d("TOTAL", "total activities: " + totalActivities.getText().toString());
        Log.d("TOTAL", "total flights: " + totalFlights.getText().toString());

        // remove the first character ($ won't always be there, it might be a different currency symbol)
        String flightsString = totalFlights.getText().toString().substring(1).replace(",", "");
        Double currentFlights = Double.parseDouble(flightsString);

        String activitiesString = totalActivities.getText().toString().substring(1).replace(",", "");
        Double currentActivities = Double.parseDouble(activitiesString);

        String hotelsString = totalHotels.getText().toString().substring(1).replace(",", "");
        Double currentHotels = Double.parseDouble(hotelsString);

        String currency = Auth.getUser().getCurrencySymbol();

        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        double totalActivitiesValue = currentActivities + totals.get("activities");
        double totalHotelsValue = currentHotels + totals.get("hotels");
        double totalFlightsValue = currentHotels + totals.get("flights");

        totalActivities.setText(currency + formatter.format(totalActivitiesValue));
        totalHotels.setText(currency + formatter.format(totalHotelsValue));
        totalFlights.setText(currency + formatter.format(totalFlightsValue));

        Double concluded = currentActivities + currentHotels + currentFlights + totals.get("activities") + totals.get("hotels") + totals.get("flights");

        TextView totalBalance = view.findViewById(R.id.total_balance);

        totalBalance.setText(String.format(currency + formatter.format(concluded)));
    }
}