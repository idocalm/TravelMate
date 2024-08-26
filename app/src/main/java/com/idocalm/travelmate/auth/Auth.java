package com.idocalm.travelmate.auth;


import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.HomeActivity;
import com.idocalm.travelmate.MainActivity;
import com.idocalm.travelmate.RegisterActivity;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public class Auth {


    public static void login(String email, String password, Runnable onSuccess, Runnable onFailure) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(authResult -> {
                onSuccess.run();
            })
            .addOnFailureListener(e -> {
                register(email, password, onSuccess, onFailure);
            });
    }

    public static void register(String email, String password, Runnable onSuccess, Runnable onFailure) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(authResult -> {
                onSuccess.run();
            })
            .addOnFailureListener(e -> {
                onFailure.run();
            });
    }

    public static boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

}