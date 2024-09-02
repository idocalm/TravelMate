package com.idocalm.travelmate.components.explore;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

import com.idocalm.travelmate.R;
import com.idocalm.travelmate.models.Hotel;

import com.idocalm.travelmate.api.Hotels;
import com.idocalm.travelmate.dialogs.PersonsDialog;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class HotelsSearchFragment extends Fragment implements DatePickerDialog.OnDateSetListener, DatePickerDialog.OnCancelListener {

    static Button peopleAmount;

    static int amount = 1;
    Date checkIn, checkOut;

    Button checkInDate, checkOutDate, search;
    boolean checkInSelected = false;
    boolean checkOutSelected = false;
    AutoCompleteTextView location;
    ListView hotelsList;
    HotelsListAdapter adapter;

    public HotelsSearchFragment() {
        // Required empty public constructor
    }

    public static void setPeopleAmount(int update) {
        amount =  update;
        if (amount == 1) {
            peopleAmount.setText("1 Person");
        } else {
            peopleAmount.setText(amount + " People");
        }
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotels_search, container, false);

        location = view.findViewById(R.id.countries);
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.countries));
        location.setAdapter(locationAdapter);

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
            PersonsDialog dialog = new PersonsDialog(getActivity());
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();

        });


        final ArrayList<Hotel> hotels = new ArrayList<>();

        hotelsList = view.findViewById(R.id.hotels_list);

        adapter = new HotelsListAdapter(getContext(), hotels);
        hotelsList.setAdapter(adapter);

        return view;

    }

    private void searchHotels() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Hotel[] hotels = Hotels.fetchHotels(location.getText().toString(), amount, checkIn, checkOut);
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

