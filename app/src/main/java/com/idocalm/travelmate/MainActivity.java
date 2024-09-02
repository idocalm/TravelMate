package com.idocalm.travelmate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.enums.CurrencyType;
import com.idocalm.travelmate.models.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button loginButton;
    Button googleButton;
    EditText emailEditText;
    EditText passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);

        loginButton = findViewById(R.id.login);
        googleButton = findViewById(R.id.google_sign_in);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
    }

    @Override
    protected void onStart() {
        super.onStart();


        if (Auth.isLoggedIn()) {
            DocumentReference ref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            CurrencyType currencyType = CurrencyType.NONE;
                            if (document.getString("currency").equals("USD")) {
                                currencyType = CurrencyType.USD;
                            } else if (document.getString("currency").equals("EUR")) {
                                currencyType = CurrencyType.EUR;
                            } else if (document.getString("currency").equals("ILS")) {
                                currencyType = CurrencyType.ILS;
                            }

                            ArrayList<String> trips = (ArrayList<String>) document.get("tripIds");

                            Auth.instantiateUser(document.getString("name"), currencyType, FirebaseAuth.getInstance().getCurrentUser().getUid(), trips);
                            startActivity(intent);
                            finish();
                        } else {



                            Auth.instantiateUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Log.d("MainActivity", "get failed with ", task.getException());
                    }
                }
            });

        }

        loginButton.setOnClickListener(v -> {
            if ( emailEditText.getText().toString().isEmpty()) {
                Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
                return;
            }
            String email = emailEditText.getText().toString();

            if (passwordEditText.getText().toString().isEmpty()) {
                Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
                return;
            }
            String password = passwordEditText.getText().toString();
            Auth.login(email, password, () -> {
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }, () -> {
                // Show error message
                Toast.makeText(this, "Login failed - ", Toast.LENGTH_SHORT).show();
            });
        });

    }


}