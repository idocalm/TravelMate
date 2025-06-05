package com.idocalm.travelmate.components.explore;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import java.text.Normalizer;
import java.util.regex.Pattern;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.adapters.HotelsListAdapter;
import com.idocalm.travelmate.models.Hotel;

import com.idocalm.travelmate.api.Hotels;
import com.idocalm.travelmate.dialogs.PersonsDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HotelsSearchFragment extends Fragment implements DatePickerDialog.OnDateSetListener, DatePickerDialog.OnCancelListener {

    static Button peopleAmount;

    static int amount = 1;
    Date checkIn, checkOut;

    Button checkInDate, checkOutDate, search;
    boolean checkInSelected = false;
    boolean checkOutSelected = false;
    AutoCompleteTextView countries, city;
    ListView hotelsList;
    HotelsListAdapter adapter;
    ProgressBar loading;
    private Map<String, List<String>> countryCityMap = new HashMap<>();

    public HotelsSearchFragment() {}

    public static void setPeopleAmount(int update) {
        amount =  update;
        if (amount == 1) {
            peopleAmount.setText("1 Person");
        } else {
            peopleAmount.setText(amount + " People");
        }
    }

    private void fetchCountriesAndCities() {
        loading.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = "https://countriesnow.space/api/v0.1/countries";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> new Thread(() -> {
                    try {
                        JSONArray dataArray = response.getJSONArray("data");
                        List<String> countryList = new ArrayList<>();
                        Map<String, List<String>> tempMap = new HashMap<>();

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject countryObj = dataArray.getJSONObject(i);
                            String countryName = countryObj.getString("country");
                            JSONArray citiesArray = countryObj.getJSONArray("cities");

                            List<String> cities = new ArrayList<>();
                            for (int j = 0; j < citiesArray.length(); j++) {
                                String rawCity = citiesArray.getString(j);
                                cities.add(normalizeToAscii(rawCity));
                            }

                            synchronized (this) {
                                tempMap.put(countryName, cities);
                                countryList.add(countryName);
                            }

                            // Optional: throttle or batch process
                            if (i % 20 == 0) {
                                Thread.sleep(10); // brief yield for GC/UI
                            }
                        }

                        // Post to UI thread
                        if (getActivity() != null)
                            requireActivity().runOnUiThread(() -> {
                                countryCityMap.clear();
                                countryCityMap.putAll(tempMap);

                                ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(
                                        requireContext(),
                                        android.R.layout.simple_list_item_1,
                                        countryList
                                );
                                countries.setAdapter(countryAdapter);
                                countries.setVisibility(View.VISIBLE);
                                loading.setVisibility(View.GONE);
                            });

                    } catch (JSONException | InterruptedException e) {
                        e.printStackTrace();
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Error processing data", Toast.LENGTH_SHORT).show()
                        );
                    }
                }).start(),
                error -> {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to fetch country data", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(request);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String selectedDate = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());

        if (checkInSelected) {
            checkIn = calendar.getTime();
            checkInDate.setText(selectedDate);
            checkInSelected = false;
        } else if (checkOutSelected) {
            checkOut = calendar.getTime();
            checkOutDate.setText(selectedDate);
            checkOutSelected = false;
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public static String normalizeToAscii(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").replaceAll("[^\\p{ASCII}]", "");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotels_search, container, false);

        loading = view.findViewById(R.id.loading_spinner);
        countries = view.findViewById(R.id.dest_country);
        city = view.findViewById(R.id.dest_city);
        city.setVisibility(View.GONE);
        countries.setVisibility(View.GONE);

        fetchCountriesAndCities();

        countries.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedCountry = (String) parent.getItemAtPosition(position);
            if (selectedCountry != null && countryCityMap.containsKey(selectedCountry)) {
                List<String> cities = countryCityMap.get(selectedCountry);
                if (cities != null && !cities.isEmpty()) {
                    city.setVisibility(View.VISIBLE);
                    ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(
                            getContext(),
                            android.R.layout.simple_list_item_1,
                            cities
                    );
                    city.setAdapter(cityAdapter);
                } else {
                    Toast.makeText(getContext(), "No cities found for selected country", Toast.LENGTH_SHORT).show();
                }
            }
        });

        search = view.findViewById(R.id.search);
        search.setOnClickListener(v -> {
            try {

                searchHotels();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        checkIn = Calendar.getInstance().getTime();
        String checkInStr = new SimpleDateFormat("dd/MM/yyyy").format(checkIn);

        checkOut = Calendar.getInstance().getTime();
        String checkOutStr = new SimpleDateFormat("dd/MM/yyyy").format(checkOut);


        checkInDate = view.findViewById(R.id.check_in);
        checkOutDate = view.findViewById(R.id.check_out);

        checkInDate.setText(checkInStr);
        checkOutDate.setText(checkOutStr);


        checkInDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(getContext(), this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            dialog.show();
            checkInSelected = true;
        });

        checkOutDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(getContext(), this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            dialog.show();
            checkOutSelected = true;
        });

        peopleAmount = view.findViewById(R.id.amount);
        setPeopleAmount(amount);

        peopleAmount.setOnClickListener(v -> {
            PersonsDialog dialog = new PersonsDialog(getActivity(), "hotels");
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();

        });


        final ArrayList<Hotel> hotels = new ArrayList<>();

        hotelsList = view.findViewById(R.id.hotels_list);

        adapter = new HotelsListAdapter(getContext(), hotels, false, null, false);
        hotelsList.setAdapter(adapter);

        return view;

    }

    private void searchHotels() throws IOException {
        loading.setVisibility(View.VISIBLE);
        hotelsList.setVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String query = city.getText().toString() + ", " + countries.getText().toString();
                    Hotel[] hotels = Hotels.fetchHotels(query, amount, checkIn, checkOut);

                    if (hotels == null)
                        hotels = new Hotel[0];

                    ArrayList<Hotel> list = new ArrayList<>();
                    Collections.addAll(list, hotels);

                    getActivity().runOnUiThread(() -> {
                        adapter.clear();
                        adapter.addAll(list);
                        adapter.notifyDataSetChanged();
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        // listen to adapter changes
        adapter.registerDataSetObserver(new android.database.DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (adapter.getCount() == 0) {
                    loading.setVisibility(View.GONE);
                    hotelsList.setVisibility(View.GONE);
                } else {
                    loading.setVisibility(View.GONE);
                    hotelsList.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    @Override
    public void onCancel(DialogInterface dialog) {
        if (checkInSelected) {
            checkInSelected = false;
        } else if (checkOutSelected) {
            checkOutSelected = false;
        }
    }
}

