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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.enums.CurrencyType;
import com.idocalm.travelmate.auth.Auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    String[] currencies = new String[] {"USD", "EUR", "ILS"};
    AutoCompleteTextView currencyCompleteTextView;
    ArrayAdapter<String> currencyAdapter;

    CurrencyType currency = CurrencyType.NONE;
    EditText nameEditText;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.d("RegisterActivity", "User not logged in, redirecting to login screen.");
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
            return;
        } else {
            Log.d("RegisterActivity", "User logged in, proceeding with registration.");
        }

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

            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            user.put("profile", FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null ? FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString() :
                    "https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png?20150327203541");
            user.put("currency", currency.toString());
            user.put("tripIds", new ArrayList<String>());
            user.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
            user.put("friendsIds", new ArrayList<String>());

            FirebaseFirestore.getInstance().collection("users").document(Auth.getUser().getId()).set(user).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(this, "Failed to register user", Toast.LENGTH_SHORT).show();
                } else {
                    Auth.instantiateUser(this);
                }
            });


        });

    }
}