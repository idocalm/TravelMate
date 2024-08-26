package com.idocalm.travelmate;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.idocalm.travelmate.enums.Currencies;

public class RegisterActivity extends AppCompatActivity {

    String[] currencies = new String[] {"USD", "EUR", "ILS"};
    AutoCompleteTextView currencyCompleteTextView;
    ArrayAdapter<String> currencyAdapter;

    Currencies currency = Currencies.NONE;
    EditText nameEditText;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        submitButton = findViewById(R.id.submit);
        currencyCompleteTextView = findViewById(R.id.currency);
        nameEditText = findViewById(R.id.name);


        currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currencies);
        currencyCompleteTextView.setAdapter(currencyAdapter);

        currencyCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currency = currencyAdapter.getItem(position);
                if (currency.equals("USD")) {
                    RegisterActivity.this.currency = Currencies.USD;
                } else if (currency.equals("EUR")) {
                    RegisterActivity.this.currency = Currencies.EUR;
                } else if (currency.equals("ILS")) {
                    RegisterActivity.this.currency = Currencies.ILS;
                }
            }
        });

        submitButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currency == Currencies.NONE) {
                Toast.makeText(this, "Currency is required", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Currency selected: " + currency, Toast.LENGTH_SHORT).show();
        });

    }
}