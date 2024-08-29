package com.idocalm.travelmate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.enums.CurrencyType;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.models.User;

public class RegisterActivity extends AppCompatActivity {

    String[] currencies = new String[] {"USD", "EUR", "ILS"};
    AutoCompleteTextView currencyCompleteTextView;
    ArrayAdapter<String> currencyAdapter;

    CurrencyType currency = CurrencyType.NONE;
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
                String item = currencyAdapter.getItem(position);
                if (item.equals("USD")) {
                    currency = CurrencyType.USD;
                } else if (item.equals("EUR")) {
                    currency = CurrencyType.EUR;
                } else if (item.equals("ILS")) {
                    currency = CurrencyType.ILS;
                }
            }
        });

        submitButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currency == CurrencyType.NONE) {
                Toast.makeText(this, "Currency is required", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("Reg", Auth.getUser().getId());

            Auth.getUser().setName(name);
            Auth.getUser().setCurrencyType(currency);

            FirebaseFirestore.getInstance().collection("users").document(Auth.getUser().getId()).set(Auth.getUser()).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(this, "Failed to register user", Toast.LENGTH_SHORT).show();
                } else {
                    Auth.getUser().setName(name);
                    Auth.getUser().setCurrencyType(currency);
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            });


        });

    }
}