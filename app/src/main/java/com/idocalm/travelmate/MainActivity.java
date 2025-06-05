package com.idocalm.travelmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.idocalm.travelmate.auth.Auth;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    public GoogleSignInClient mGoogleSignInClient;
    Button loginButton;
    Button googleButton;
    EditText emailEditText;
    EditText passwordEditText;
    private Consumer<GoogleSignInAccount> successHandler;
    private Runnable failureHandler;
    public static final int RC_SIGN_IN = 1001;

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

    public void setSignInResultHandler(Consumer<GoogleSignInAccount> successHandler, Runnable failureHandler) {
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (successHandler != null) successHandler.accept(account);
            } catch (ApiException e) {
                if (failureHandler != null) failureHandler.run();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        googleButton.setOnClickListener(v -> {
            Auth.loginWithGoogle(this, () -> {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            }, () -> {
                // Show error message
                Toast.makeText(this, "Login failed - ", Toast.LENGTH_SHORT).show();
            });
        });

        loginButton.setOnClickListener(v -> {
            if (emailEditText.getText().toString().isEmpty()) {
                Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
                return;
            }
            String email = emailEditText.getText().toString();

            if (passwordEditText.getText().toString().isEmpty()) {
                Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
                return;
            }

            String password = passwordEditText.getText().toString();


            Auth.login(this, email, password, () -> {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

            }, () -> {
                // Show error message
                Toast.makeText(this, "Login failed - ", Toast.LENGTH_SHORT).show();
            });
        });

    }


}