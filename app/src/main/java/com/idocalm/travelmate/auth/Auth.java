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
import com.idocalm.travelmate.enums.CurrencyType;
import com.idocalm.travelmate.models.User;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public class Auth {

    private static volatile User instance = null;

    public static User instantiateUser(String name, CurrencyType currencyType, String id, ArrayList<String> trips) {
        if (instance == null) {
            synchronized (User.class) {
                if (instance == null) {
                    instance = new User(name, currencyType, id, trips);
                }
            }
        }
        return instance;
    }


    public static User instantiateUser(String id) {
        if (instance == null) {
            synchronized (User.class) {
                if (instance == null) {
                    instance = new User("", CurrencyType.NONE, id, new ArrayList<>());
                }
            }
        }
        return instance;
    }

    public static User getUser() {
        return instance;
    }

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
                instance = instantiateUser(authResult.getUser().getUid());
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
