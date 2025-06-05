package com.idocalm.travelmate.components.explore;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.idocalm.travelmate.R;
import com.idocalm.travelmate.adapters.FlightsListAdapter;
import com.idocalm.travelmate.api.FlightCallback;
import com.idocalm.travelmate.api.Flights;
import com.idocalm.travelmate.dialogs.PersonsDialog;
import com.idocalm.travelmate.models.Flight;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FlightsSearchFragment extends Fragment implements DatePickerDialog.OnDateSetListener, DatePickerDialog.OnCancelListener {

    static Button peopleAmount;
    static int amount = 1;
    Date flightDate;

    Button date, search;
    AutoCompleteTextView origin, destination;
    FlightsListAdapter adapter;
    LinearLayout loading;

    public FlightsSearchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_flights_search, container, false);

        peopleAmount = view.findViewById(R.id.people_amount);
        loading = view.findViewById(R.id.loading_spinner);
        loading.setVisibility(View.GONE);

        origin = view.findViewById(R.id.org_country);
        destination = view.findViewById(R.id.dest_country);

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.countries));
        destination.setAdapter(locationAdapter);
        origin.setAdapter(locationAdapter);

        search = view.findViewById(R.id.search);
        search.setOnClickListener(v -> searchFlights());

        flightDate = Calendar.getInstance().getTime();
        String dateStr = new SimpleDateFormat("dd/MM/yyyy").format(flightDate);

        date = view.findViewById(R.id.flight_date);
        date.setText(dateStr);

        date.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(getContext(), this,
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        setPeopleAmount(amount);

        peopleAmount.setOnClickListener(v -> {
            PersonsDialog dialog = new PersonsDialog(getActivity(), "flights");
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
        });

        return view;

    }

    private void searchFlights() {
        loading.setVisibility(View.VISIBLE);

        String from = origin.getText().toString();
        String to = destination.getText().toString();

        if (from.isEmpty() || to.isEmpty()) {
            Toast.makeText(getContext(), "Please select a departure and destination", Toast.LENGTH_SHORT).show();
            loading.setVisibility(View.GONE);
            return;
        }

        Flights.fetchFlights(
                getActivity(),
                from,
                to,
                amount,
                flightDate,
                new FlightCallback() {
                    @Override
                    public void onFlightsFetched(ArrayList<Flight> flights) {
                        loading.setVisibility(View.GONE);
                        showResults(flights);
                    }

                    @Override
                    public void onError(Exception e) {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error fetching flights: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
        );
    }

    private void showResults(ArrayList<Flight> flights) {
        ListView listView = getView().findViewById(R.id.flights_list);
        adapter = new FlightsListAdapter(getContext(), flights, false, null, false);
        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);
    }

    public static void setPeopleAmount(int update) {
        amount = update;
        if (peopleAmount != null) {
            if (amount == 1) {
                peopleAmount.setText("1 Person");
            } else {
                peopleAmount.setText(amount + " People");
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        flightDate = calendar.getTime();

        String selectedDate = new SimpleDateFormat("dd/MM/yyyy").format(flightDate);
        date.setText(selectedDate);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
    }
}
